# upgrade

Upgrade Coding challenge
This project aims at building a reservation system for a campsite with few constraints 

* The campsite will be free for all.
* The campsite can be reserved for max 3 days.
* The campsite can be reserved minimum 1 day(s) ahead of arrival and up to 1 month in advance. Reservations can be cancelled anytime.
* For sake of simplicity assume the check-in & check-out time is 12:00 AM



System Requirements: 

* The users will need to find out when the campsite is available. So the system should expose an API to provide information of the availability of the campsite for a given date range with the default being 1 month.
* Provide an end point for reserving the campsite. The user will provide his/her email & full name at the time of reserving the campsite along with intended arrival date and departure date. Return a unique booking identifier back to the caller if the reservation is successful. The unique booking identifier can be used to modify or cancel the reservation later on. Provide appropriate end point(s) to allow modification/cancellation of an existing reservation
* Due to the popularity of the island, there is a high likelihood of multiple users attempting to reserve the campsite for the same/overlapping date(s). Demonstrate with appropriate test cases that the system can gracefully handle concurrent requests to reserve the campsite. Provide appropriate error messages to the caller to indicate the error cases.
* In general, the system should be able to handle large volume of requests for getting the campsite availability.
* There are no restrictions on how reservations are stored as as long as system constraints are not violated.


Persistence: MySQL db


How to run the project: 
1) Download repo 
2) Open application.properties file under src/main/resources and replace values for 
    * spring.datasource.url with correct mysql url 
    * spring.datasource.username with username for mysql url 
3) After updating the values, 
    * Go to the location of the project --> cd upgrade 
    * Run: mvn clean install --> Make sure that the build is successfull
4) After mvn clean install is successfull, there are two ways to run the project 
    * Run: mvn exec:java  --> this would start the application and then use the API mentioned below to get the response. 
    * Run: java -jar target/upgrade-0.0.1-SNAPSHOT.jar --> this would start the application and then use the API mentioned below to get the response. 
  





API 


1) Create reservation: 

Request 
curl -i -X POST 'http://localhost:8080/v1/reservations' -d '{"name":"Ankur Gupta", "email":"ankurgupta851@gmail.com", "reservationStartDate":"2019-09-15", "reservationEndDate":"2019-09-17"}' -H 'Content-type: application/json'


Response 
{
  "id": 4,
  "email": "ankurgupta851@gmail.com",
  "name": "Ankur Gupta",
  "reservationIndex": "20190915-20190917",
  "reservationStartTimestamp": 20190915,
  "reservationEndTimestamp": 20190917,
  "reservationStartDate": "2019-09-15",
  "reservationEndDate": "2019-09-17",
  "daysReserved": 2,
  "createDate": "2019-09-02"
}




2) Get reservation

Request 
curl -X GET 'http://localhost:8080/v1/reservations/1' -H 'Content-type: application/json'

Response 
{
  "id": 1,
  "email": "ankurgupta851@gmail.com",
  "name": "Ankur Gupta",
  "reservationIndex": "20190910-20190913",
  "reservationStartTimestamp": 20190910,
  "reservationEndTimestamp": 20190913,
  "reservationStartDate": "2019-09-10",
  "reservationEndDate": "2019-09-13",
  "daysReserved": 3,
  "createDate": "2019-09-02T00:00:00.000+0000"
}

Request with invalid identifier
curl -X GET 'http://localhost:8080/v1/reservations/101' -H 'Content-type: application/json'

Response 
Reservation does not exists



3) Get all reservations

Request 
curl -X GET 'http://localhost:8080/v1/reservations' -H 'Content-type: application/json'


Response 

[
  {
    "id": 1,
    "email": "ankurgupta851@gmail.com",
    "name": "Ankur Gupta",
    "reservationIndex": "20190910-20190913",
    "reservationStartTimestamp": 20190910,
    "reservationEndTimestamp": 20190913,
    "reservationStartDate": "2019-09-10",
    "reservationEndDate": "2019-09-13",
    "daysReserved": 3,
    "createDate": "2019-09-02T00:00:00.000+0000"
  },
  {
    "id": 3,
    "email": "ankurgupta851@gmail.com",
    "name": "Ankur Gupta",
    "reservationIndex": "20190904-20190907",
    "reservationStartTimestamp": 20190904,
    "reservationEndTimestamp": 20190907,
    "reservationStartDate": "2019-09-04",
    "reservationEndDate": "2019-09-07",
    "daysReserved": 3,
    "createDate": "2019-09-03T00:00:00.000+0000"
  }
]


4) Get availability 

Request 
curl -X GET 'http://localhost:8080/v1/reservations/availability' -H 'Content-type: application/json'


Response 
["2019-09-07","2019-09-08","2019-09-12","2019-09-13","2019-09-14","2019-09-18","2019-09-19","2019-09-20","2019-09-21","2019-09-22","2019-09-23","2019-09-24","2019-09-25","2019-09-26","2019-09-27","2019-09-28","2019-09-29","2019-09-30","2019-10-01","2019-10-02","2019-10-03","2019-10-04"]


Request with arguments 
curl -X GET 'http://localhost:8080/v1/reservations/availability?startDate=2019-09-15&endDate=2019-10-15' -H 'Content-type: application/json'


Response 
[
  "2019-09-18",
  "2019-09-19",
  "2019-09-20",
  "2019-09-21",
  "2019-09-22",
  "2019-09-23",
  "2019-09-24",
  "2019-09-25",
  "2019-09-26",
  "2019-09-27",
  "2019-09-28",
  "2019-09-29",
  "2019-09-30",
  "2019-10-01",
  "2019-10-02",
  "2019-10-03",
  "2019-10-04",
  "2019-10-05",
  "2019-10-06",
  "2019-10-07",
  "2019-10-08",
  "2019-10-09",
  "2019-10-10",
  "2019-10-11",
  "2019-10-12",
  "2019-10-13",
  "2019-10-14",
  "2019-10-15"
]


5) Cancel/Delete the reservation 

Request 
curl -X DELETE 'http://localhost:8080/v1/reservations/1' -H 'Content-type: application/json'

Response 
true



6) Update the reservation 

Request 
curl -i -X PUT 'http://localhost:8080/v1/reservations/3' -d '{"name":"Ankur Gupta", "email":"ankurgupta851@gmail.com", "reservationStartDate":"2019-09-09", "reservationEndDate":"2019-09-11"}' -H 'Content-type: application/json'

Response 

{
  "id": 3,
  "email": "ankurgupta851@gmail.com",
  "name": "Ankur Gupta",
  "reservationIndex": "20190909-20190911",
  "reservationStartTimestamp": 20190909,
  "reservationEndTimestamp": 20190911,
  "reservationStartDate": "2019-09-09",
  "reservationEndDate": "2019-09-11",
  "daysReserved": 2,
  "createDate": "2019-09-03T00:00:00.000+0000",
  "updateDate": "2019-09-02"
}
