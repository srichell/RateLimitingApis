# RateLimitingApis
Rate Limiting an API call, based on an API key



Tests Done
----------
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
         