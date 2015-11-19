![easyfit](https://raw.github.com/nikoudel/easyfit/images/logo.png)

Easyfit is a connector between [FitNesse](http://fitnesse.org) and a System Under Test (SUT). It transforms tabular data from FitNesse tables into HTTP calls to SUT:

![](https://raw.githubusercontent.com/nikoudel/easyfit/images/cars.png FitNesse tables)

	POST http://localhost:48080/CreateCar.json: {"id":null,"make":"VW","model":"Passat","color":"red","registrationDate":"2010-11-25","mileage":"117000","fuel":"Diesel"} -> {"id":1,"make":"VW","model":"Passat","color":"red","registrationDate":1290643200000,"mileage":117000,"fuel":"Diesel"}
	POST http://localhost:48080/CreateCar.json: {"id":null,"make":"VW","model":"Bora","color":"red","registrationDate":"2004-04-02","mileage":"176000","fuel":"Petrol"} -> {"id":2,"make":"VW","model":"Bora","color":"red","registrationDate":1080864000000,"mileage":176000,"fuel":"Petrol"}
	POST http://localhost:48080/CreateCar.json: {"id":null,"make":"VW","model":"Jetta","color":"blue","registrationDate":"2007-07-22","mileage":"123400","fuel":"Petrol"} -> {"id":3,"make":"VW","model":"Jetta","color":"blue","registrationDate":1185062400000,"mileage":123400,"fuel":"Petrol"}
	POST http://localhost:48080/CreateCar.json: {"id":null,"make":"Toyota","model":"Corolla","color":"green","registrationDate":"2012-01-11","mileage":"64200","fuel":"Petrol"} -> {"id":4,"make":"Toyota","model":"Corolla","color":"green","registrationDate":1326240000000,"mileage":64200,"fuel":"Petrol"}
	GET http://localhost:48080/GetCars.json: [{"id":1,"make":"VW","model":"Passat","color":"red","registrationDate":1290643200000,"mileage":117000,"fuel":"Diesel"},{"id":2,"make":"VW","model":"Bora","color":"red","registrationDate":1080864000000,"mileage":176000,"fuel":"Petrol"},{"id":3,"make":"VW","model":"Jetta","color":"blue","registrationDate":1185062400000,"mileage":123400,"fuel":"Petrol"},{"id":4,"make":"Toyota","model":"Corolla","color":"green","registrationDate":1326240000000,"mileage":64200,"fuel":"Petrol"}]

Getting started
===============
- Clone or [download](https://github.com/nikoudel/easyfit/archive/master.zip) this project
- Start FitNesse and testWebService by running start-fitnesse.bat and start-web-service.bat in the testWebService\install folder
- Go to http://localhost:8080 and run the test

The test represents an example of initializing a SUT (testWebService) with a set of items and executing queries with parameters against those items. Wire data can be observed from easyFit.log in testWebService\install.

The only required file to utilize easyFit is easyFit\install\easyFit.jar. The demo test also adds filters.jar to classpath but it is included only to demonstrate how to transform customly formatted data (unix time stamp) into a human-readable format.

Building the project
====================
To build the project install [Gradle](http://gradle.org/gradle-download/) and run "gradle install" in the root folder.