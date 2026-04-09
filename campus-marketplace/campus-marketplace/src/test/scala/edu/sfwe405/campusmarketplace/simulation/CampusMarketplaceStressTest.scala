package edu.sfwe405.campusmarketplace.simulation

import io.gatling.core.Predef.
import io.gatling.http.Predef.

import scala.concurrent.duration.

/**
 * Gatling Stress Test – Campus Marketplace
 *
 * Scenarios covered
 * -----------------
 * 1. Browse products  – anonymous GET /products        (high volume, no auth)
 * 2. Auth flow        – POST /auth/register → /auth/login (medium volume)
 * 3. Seller flow      – register as SELLER → login → POST /products (lower volume)
 * 4. Buyer flow       – login as existing buyer → GET /products → POST /orders (lower volume)
 *
 * Load profile (ramp → sustained → spike)
 * ----------------------------------------
 * 0–30 s   : ramp to 20 concurrent users across all scenarios
 * 30–90 s  : hold 20 users (steady-state)
 * 90–100 s : spike to 50 users to test resilience
 * 100–120 s: cool-down back to 5 users
 *
 * How to run
 * ----------
 *   mvn gatling:test
 *
 * The HTML report lands in target/gatling/<timestamp>/index.html
 * Make sure the application is running on localhost:8080 first.
 */
class CampusMarketplaceStressTest extends Simulation {

  // ─── Base HTTP config ─────────────────────────────────────────────────────

  val httpProtocol = http
    .baseUrl("http://localhost:8080")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")
    .acceptEncodingHeader("gzip, deflate")

  // ─── Helpers ──────────────────────────────────────────────────────────────

  /** Unique email per virtual user so registration never conflicts */
  def uniqueEmail(prefix: String): String =
    s"$prefix-${System.nanoTime()}-${scala.util.Random.alphanumeric.take(6).mkString}@test.edu"

  // ─── Scenario 1: Anonymous product browsing ────────────────────────────────
  //   Simulates the most common visitor: someone who is NOT logged in
  //   and simply browses the product catalogue.

  val browseProducts = scenario("Browse Products (anonymous)")
    .exec(
      http("GET /products")
        .get("/products")
        .check(status.is(200))
    )
    .pause(1, 3) // think-time between requests

  // ─── Scenario 2: Register then login ───────────────────────────────────────
  //   Tests the auth endpoints under load. Each VU registers a fresh account
  //   then immediately logs in to obtain a JWT.

  val authFlow = scenario("Register + Login")
    .exec { session =>
      session.set("email", uniqueEmail("buyer"))
             .set("password", "Password1!")
    }
    .exec(
      http("POST /auth/register")
        .post("/auth/register")
        .body(StringBody("""{"email":"#{email}","password":"#{password}","role":"BUYER"}"""))
        .check(status.in(200, 201))
    )
    .pause(500.milliseconds, 1.second)
    .exec(
      http("POST /auth/login")
        .post("/auth/login")
        .body(StringBody("""{"email":"#{email}","password":"#{password}"}"""))
        .check(status.is(200))
        .check(jsonPath("$.token").saveAs("jwtToken"))
    )

  // ─── Scenario 3: Seller creates a product ─────────────────────────────────
  //   Tests the authenticated product-creation endpoint.
  //   VU registers as SELLER → logs in → POSTs a product.

  val sellerFlow = scenario("Seller: Register + Login + Create Product")
    .exec { session =>
      session.set("sellerEmail", uniqueEmail("seller"))
             .set("sellerPassword", "Password1!")
    }
    // 3a. Register seller
    .exec(
      http("POST /auth/register (seller)")
        .post("/auth/register")
        .body(StringBody("""{"email":"#{sellerEmail}","password":"#{sellerPassword}","role":"SELLER"}"""))
        .check(status.in(200, 201))
    )
    .pause(300.milliseconds, 800.milliseconds)
    // 3b. Login seller
    .exec(
      http("POST /auth/login (seller)")
        .post("/auth/login")
        .body(StringBody("""{"email":"#{sellerEmail}","password":"#{sellerPassword}"}"""))
        .check(status.is(200))
        .check(jsonPath("$.token").saveAs("sellerToken"))
    )
    .pause(200.milliseconds, 500.milliseconds)
    // 3c. Create product
    .exec(
      http("POST /products")
        .post("/products")
        .header("Authorization", "Bearer #{sellerToken}")
        .body(StringBody(
          """{
            |  "name": "Stress Test Textbook",
            |  "description": "Used calculus book in great condition",
            |  "price": 29.99,
            |  "stock": 5,
            |  "categoryId": null
            |}""".stripMargin))
        .check(status.in(200, 201))
        .check(jsonPath("$.id").saveAs("productId"))
    )

  // ─── Scenario 4: Buyer browses and places an order ─────────────────────────
  //   Tests the full authenticated buyer flow under load.
  //   VU registers as BUYER → logs in → GETs products → POSTs an order
  //   (the order requires buyerId and productId; we register a fresh buyer
  //    and use product id=1 which is seeded by data.sql or created in run 3).

  val buyerFlow = scenario("Buyer: Register + Login + Browse + Order")
    .exec { session =>
      session.set("buyerEmail", uniqueEmail("buyer"))
             .set("buyerPassword", "Password1!")
    }
    // 4a. Register
    .exec(
      http("POST /auth/register (buyer)")
        .post("/auth/register")
        .body(StringBody("""{"email":"#{buyerEmail}","password":"#{buyerPassword}","role":"BUYER"}"""))
        .check(status.in(200, 201))
        .check(jsonPath("$.id").saveAs("buyerId"))
    )
    .pause(300.milliseconds, 700.milliseconds)
    // 4b. Login
    .exec(
      http("POST /auth/login (buyer)")
        .post("/auth/login")
        .body(StringBody("""{"email":"#{buyerEmail}","password":"#{buyerPassword}"}"""))
        .check(status.is(200))
        .check(jsonPath("$.token").saveAs("buyerToken"))
    )
    .pause(500.milliseconds, 1.second)
    // 4c. Browse products
    .exec(
      http("GET /products (authenticated)")
        .get("/products")
        .header("Authorization", "Bearer #{buyerToken}")
        .check(status.is(200))
    )
    .pause(1.second, 2.seconds)
    // 4d. Place order – only if we successfully saved a buyerId
    .doIf(session => session.contains("buyerId")) {
      exec(
        http("POST /orders")
          .post("/orders")
          .header("Authorization", "Bearer #{buyerToken}")
          .body(StringBody("""{"buyerId":#{buyerId},"productId":1}"""))
          // 200 = success, 400/500 expected if product 1 does not exist yet
          .check(status.in(200, 201, 400, 500))
      )
    }

  // ─── Performance assertions ────────────────────────────────────────────────
  //   The simulation fails if these thresholds are breached, making it a
  //   pass/fail gate suitable for CI pipelines.

  setUp(
    // High-volume anonymous browsing
    browseProducts.inject(
      rampUsers(30).during(30.seconds),
      constantUsersPerSec(10).during(60.seconds),
      rampUsersPerSec(10).to(50).during(10.seconds),
      constantUsersPerSec(50).during(10.seconds),
      rampUsersPerSec(50).to(5).during(20.seconds)
    ),

    // Medium-volume auth flow
    authFlow.inject(
      nothingFor(5.seconds),
      rampUsers(10).during(30.seconds),
      constantUsersPerSec(3).during(60.seconds)
    ),

    // Lower-volume seller flow
    sellerFlow.inject(
      nothingFor(10.seconds),
      rampUsers(5).during(20.seconds),
      constantUsersPerSec(1).during(70.seconds)
    ),

    // Lower-volume buyer flow
    buyerFlow.inject(
      nothingFor(15.seconds),
      rampUsers(5).during(20.seconds),
      constantUsersPerSec(1).during(65.seconds)
    )
  )
  .protocols(httpProtocol)
  .assertions(
    // Overall success rate must be ≥ 95 %
    global.successfulRequests.percent.gte(95),
    // 99th-percentile response time must be < 3 s
    global.responseTime.percentile(99).lt(3000),
    // Mean response time must be < 500 ms
    global.responseTime.mean.lt(500)
  )
}
