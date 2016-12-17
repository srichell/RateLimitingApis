# RateLimitingApis

GIT Repo for Rate Limiting an API call, based on an API key. I used the following Tech Stack.

1. Java for Source Code development. 
2. Dropwizard for Web services. 
        DropWizard implements a lightweight Jetty Container which is a reference implementation for Jax-RS.
        Implementing Asynchronous non blocking APIs is easy using Drop Wizard (for me).
        Dropwizard provides a comprehensive Metrics Library with built-in reporters.
3. Token Bucket as the algorithm for Retalimiting.
4. Spring for Inversion of Control. Using spring gives me the capability to associate disassociate
   a component using a config.
5. TestNg for Unit tests. TestNG enables testing multi threaded code.
6. Jacoco for Generating Unit test coverage reports and SONAR for visualization. (SONAR not
   tested)

This GIT repo is fully functional and can be deployed on any Linux based box.

InfraStructure Requirements to run code
---------------------------------------
1. Java Version 1.7 or higher
2. Linux box with any flavor of linux.

How to Run
-----------
1. mvn test to run tests
2. mvn clean install to compile the Jar.
3. java -XX:+UseG1GC -XX:MaxGCPauseMillis=50 -Xmx4096m -Xms4096m -jar RateLimiting-0.0.1-SNAPSHOT.jar config.yaml &



   Thread Safety
   -------------

   The code is inherently thread safe because there are not too many shared data structures
   that are read and written to from context of different threads.  

   Metrics
   -------
   We use Dropwizard metrics to get both JVM level metrics and App level metrics. While these
   metrics can be wired into Graphite/Grafana, I have tested the metrics using :
        1. SLF4J reporter
        2. Admin Servelet (curl http://localhost:8081/metrics?pretty=true)

   HealthChecks
   ------------
   HealthChecks provide a way for a caller to know whether the service is running before making
   a call. Our service provide a basic Ping HealthCheck that can be extended to a more
   comprehensive HealthCheck Solution.

   Admin Servelet (curl http://localhost:8081/healthcheck?pretty=true)

   Thread Dumps
   ------------
   The service gives you a REST API to pull thread dumps without logging into a Box. Just do a
   curl http://localhost:8081/threads to get thread Dumps.

   Tests
   -----
   Tests are two fold.

   1. Unit Tests - Unit tests are written using TestNG test cases and are under test/main/java. You can run them
      using using mvn test (Not populated yet)
   2. Functional Tests - See below.


Functionl Tests Done
---------------------
1. Happy Case - Get Info by City
   curl 'localhost:8080/v1/hotels/rooms?apikey=abcd&city=Bangkok' 
   {"Hotel Rooms":
   [{"hotelId":1,"roomType":"DELUXE_ROOM","price":1000.0},
   {"hotelId":6,"roomType":"SUPERIOR_ROOM","price":2000.0},
   {"hotelId":8,"roomType":"SUPERIOR_ROOM","price":2400.0},
   {"hotelId":11,"roomType":"DELUXE_ROOM","price":60.0},
   {"hotelId":14,"roomType":"SWEET_SUITE_ROOM","price":25000.0},
   {"hotelId":15,"roomType":"DELUXE_ROOM","price":900.0},
   {"hotelId":18,"roomType":"SWEET_SUITE_ROOM","price":5300.0}]}
   
   
2. Happy Case - Get info by city by staying well within the rate limit

  curl 'localhost:8080/v1/hotels/rooms?apikey=abcd&city=Bangkok' ; sleep 60 ; curl 'localhost:8080/v1/hotels/rooms?apikey=abcd&city=Bangkok'

   {"Hotel Rooms":
   [{"hotelId":1,"roomType":"DELUXE_ROOM","price":1000.0},
   {"hotelId":6,"roomType":"SUPERIOR_ROOM","price":2000.0},
   {"hotelId":8,"roomType":"SUPERIOR_ROOM","price":2400.0},
   {"hotelId":11,"roomType":"DELUXE_ROOM","price":60.0},
   {"hotelId":14,"roomType":"SWEET_SUITE_ROOM","price":25000.0},
   {"hotelId":15,"roomType":"DELUXE_ROOM","price":900.0},
   {"hotelId":18,"roomType":"SWEET_SUITE_ROOM","price":5300.0}]}
   
   {"Hotel Rooms":
   [{"hotelId":1,"roomType":"DELUXE_ROOM","price":1000.0},
   {"hotelId":6,"roomType":"SUPERIOR_ROOM","price":2000.0},
   {"hotelId":8,"roomType":"SUPERIOR_ROOM","price":2400.0},
   {"hotelId":11,"roomType":"DELUXE_ROOM","price":60.0},
   {"hotelId":14,"roomType":"SWEET_SUITE_ROOM","price":25000.0},
   {"hotelId":15,"roomType":"DELUXE_ROOM","price":900.0},
   {"hotelId":18,"roomType":"SWEET_SUITE_ROOM","price":5300.0}]}
      
   
3. Error Case : Rate limit violation of tailgating requests
 
   curl 'localhost:8080/v1/hotels/rooms?apikey=abcd&city=Bangkok' ; curl -v 'localhost:8080/v1/hotels/rooms?apikey=abcd&city=Bangkok'
   
       {"Hotel Rooms":
       [{"hotelId":1,"roomType":"DELUXE_ROOM","price":1000.0},
       {"hotelId":6,"roomType":"SUPERIOR_ROOM","price":2000.0},
       {"hotelId":8,"roomType":"SUPERIOR_ROOM","price":2400.0},
       {"hotelId":11,"roomType":"DELUXE_ROOM","price":60.0},
       {"hotelId":14,"roomType":"SWEET_SUITE_ROOM","price":25000.0},
       {"hotelId":15,"roomType":"DELUXE_ROOM","price":900.0},
       {"hotelId":18,"roomType":"SWEET_SUITE_ROOM","price":5300.0}]}
       
      
   * Connected to localhost (::1) port 8080 (#0)
   > GET /v1/hotels/rooms?apikey=abcd&city=Bangkok HTTP/1.1
   > Host: localhost:8080
   > User-Agent: curl/7.43.0
   > Accept: */*
   > 
   < HTTP/1.1 429 
   < Date: Sat, 17 Dec 2016 17:18:00 GMT
   < Content-Type: text/plain
   < Content-Length: 19
   < 
   * Connection #0 to host localhost left intact

   On the app logs :
   
        2016-12-17 22:50:16 ERROR c.s.m.r.r.apis.RateLimitTokenBucketAlgorithm - Blacklisting apiKey ApiKey{apiKey='abcd'} for 5 minutes 
   
   
4. Error Recovery Case : An API key coming out of blacklist after 5 minutes (configured default)
 
   curl 'localhost:8080/v1/hotels/rooms?apikey=abcd&city=Bangkok' ; curl -v 'localhost:8080/v1/hotels/rooms?apikey=abcd&city=Bangkok' ; sleep 300; curl -v 'localhost:8080/v1/hotels/rooms?apikey=abcd&city=Bangkok'
   
       {"Hotel Rooms":
       [{"hotelId":1,"roomType":"DELUXE_ROOM","price":1000.0},
       {"hotelId":6,"roomType":"SUPERIOR_ROOM","price":2000.0},
       {"hotelId":8,"roomType":"SUPERIOR_ROOM","price":2400.0},
       {"hotelId":11,"roomType":"DELUXE_ROOM","price":60.0},
       {"hotelId":14,"roomType":"SWEET_SUITE_ROOM","price":25000.0},
       {"hotelId":15,"roomType":"DELUXE_ROOM","price":900.0},
       {"hotelId":18,"roomType":"SWEET_SUITE_ROOM","price":5300.0}]}
       
      
   * Connected to localhost (::1) port 8080 (#0)
   > GET /v1/hotels/rooms?apikey=abcd&city=Bangkok HTTP/1.1
   > Host: localhost:8080
   > User-Agent: curl/7.43.0
   > Accept: */*
   > 
   < HTTP/1.1 429 
   < Date: Sat, 17 Dec 2016 17:18:00 GMT
   < Content-Type: text/plain
   < Content-Length: 19
   < 
   * Connection #0 to host localhost left intact

   On the app logs :
   
        2016-12-17 22:50:16 ERROR c.s.m.r.r.apis.RateLimitTokenBucketAlgorithm - Blacklisting apiKey ApiKey{apiKey='abcd'} for 5 minutes 
        
        {"Hotel Rooms":
         [{"hotelId":1,"roomType":"DELUXE_ROOM","price":1000.0},
         {"hotelId":6,"roomType":"SUPERIOR_ROOM","price":2000.0},
         {"hotelId":8,"roomType":"SUPERIOR_ROOM","price":2400.0},
         {"hotelId":11,"roomType":"DELUXE_ROOM","price":60.0},
         {"hotelId":14,"roomType":"SWEET_SUITE_ROOM","price":25000.0},
         {"hotelId":15,"roomType":"DELUXE_ROOM","price":900.0},
         {"hotelId":18,"roomType":"SWEET_SUITE_ROOM","price":5300.0}]}
   
5. To see whether a renewal of credit can bring a blacklisted apiKey out of the blacklist

acbc32a5d59d ~/workspace/WhereIsMyDriver_Tests>curl 'localhost:8080/v1/hotels/rooms?apikey=abcd&city=Bangkok' ; curl -v 'localhost:8080/v1/hotels/rooms?apikey=abcd&city=Bangkok'; curl -X POST 'localhost:8080/v1/hotels/credit?apikey=abcd&credit=10'; curl -v 'localhost:8080/v1/hotels/rooms?apikey=abcd&city=Bangkok'


        {"Hotel Rooms":
         [{"hotelId":1,"roomType":"DELUXE_ROOM","price":1000.0},
         {"hotelId":6,"roomType":"SUPERIOR_ROOM","price":2000.0},
         {"hotelId":8,"roomType":"SUPERIOR_ROOM","price":2400.0},
         {"hotelId":11,"roomType":"DELUXE_ROOM","price":60.0},
         {"hotelId":14,"roomType":"SWEET_SUITE_ROOM","price":25000.0},
         {"hotelId":15,"roomType":"DELUXE_ROOM","price":900.0},
         {"hotelId":18,"roomType":"SWEET_SUITE_ROOM","price":5300.0}]}
   
* Connected to localhost (::1) port 8080 (#0)
> GET /v1/hotels/rooms?apikey=abcd&city=Bangkok HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.43.0
> Accept: */*
> 
< HTTP/1.1 429 
< Date: Sat, 17 Dec 2016 17:39:54 GMT
< Content-Type: text/plain
< Content-Length: 19
< 
* Connection #0 to host localhost left intact
RATE LIMIT EXCEEDED






*   Trying ::1...
* Connected to localhost (::1) port 8080 (#0)
> GET /v1/hotels/rooms?apikey=abcd&city=Bangkok HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.43.0
> Accept: */*
> 
< HTTP/1.1 200 OK
< Date: Sat, 17 Dec 2016 17:39:54 GMT
< Content-Type: text/plain
< Vary: Accept-Encoding
< Content-Length: 411
< 
* Connection #0 to host localhost left intact
        {"Hotel Rooms":
         [{"hotelId":1,"roomType":"DELUXE_ROOM","price":1000.0},
         {"hotelId":6,"roomType":"SUPERIOR_ROOM","price":2000.0},
         {"hotelId":8,"roomType":"SUPERIOR_ROOM","price":2400.0},
         {"hotelId":11,"roomType":"DELUXE_ROOM","price":60.0},
         {"hotelId":14,"roomType":"SWEET_SUITE_ROOM","price":25000.0},
         {"hotelId":15,"roomType":"DELUXE_ROOM","price":900.0},
         {"hotelId":18,"roomType":"SWEET_SUITE_ROOM","price":5300.0}]}
   
   
      Test Automation and Report Generation
      -------------------------------------
   
      All of these tests can be plugged into Jenkins server to be automatically run. (Not tested yet)
   
      When you run "mvn test", Test Coverage reports are automatically generated. For detailed coverage reports,
      please look at $ROOT/target/jacoco/jacoco.exec/jacoco-ut/index.html. A SONAR server can be setup for
      Graphical visualization.
      
      
