![easyfit](https://raw.github.com/nikoudel/easyfit/images/logo.png)

Easyfit is a [FitNesse](http://fitnesse.org) plugin. Its main goal is to lower the threshold of creating and attaching FitNesse tests to an existing system under test (SUT). Easyfit was built with the following objectives in mind:

1. reduce boilerplate code to minimum by utilizing external libraries for data serialization
2. provide only two tables to access SUT in order to make learning easier and misusing harder
3. make these tables flexible enough to get the job done in a convenient way
4. use a standard communication protocol between FitNesse and SUT

Easyfit takes advantage of RESTful frameworks in several ways. First, it utilizes http(s) protocol for SUT communication making it suitable for testing any kind of applications built on platforms with decent REST support (eg. [ServiceStack][ss] for .NET and [Restlet](http://restlet.org) for Java). Second, the frameworks reduce maintenance effort by handling the boring and error-prone task of test data (de)serialization and routing (the boilerplate code mentioned above). Finally, although Easyfit doesn't force the user to apply the classical [RESTful approach](http://en.wikipedia.org/wiki/Representational_state_transfer), it makes it easy to adopt one, possibly leading to a cleaner application API and overall architecture, especially with [TDD](http://en.wikipedia.org/wiki/Test-driven_development) process.

Easyfit has only two tables handling all SUT communication (Query and Row). These two tables incapsulate the [CRUD](http://en.wikipedia.org/wiki/Create,_read,_update_and_delete) operations enabling interactions with systems of any complexity.

Query table is comparable to SQL *select* operation in terms of *structure* of the data being exchanged. It gets a list of search arguments and passes them to SUT which, in turn, returns zero or more items (rows), just like the Slim [Query Table](http://fitnesse.org/FitNesse.UserGuide.SliM.QueryTable). *However*, it shouldn't be considered only a Read operation – it's called "query" only because the amount of rows to be returned is uncertain. Query table can still be used, for example, for deleting a bunch of items based on certain criteria and returning a list of ID's of the deleted items.

Row table corresponds to Slim [Decision Table](http://fitnesse.org/FitNesse.UserGuide.SliM.DecisionTable); it calls SUT row by row so that, on each call, there is exactly one row (item) going into the SUT and exactly one row coming out of it.

So, how these tables differ from FitNesse Slim tables? Essentially, they don't. But they are the only two tables necessary to utilize the http GET and POST methods which is the inherent part of this project. They also have some additional features such as symbol (variable) initialization inside the Query table and input/output value filters. The details will be described next.

Getting started
===============

The easiest way to try easyfit is to [download](https://github.com/nikoudel/SmartHome/archive/master.zip) or clone the [SmartHome](https://github.com/nikoudel/SmartHome) demo project:

    git clone https://github.com/nikoudel/SmartHome.git

The project is a .NET console application demonstrating all easyfit features. SmartHome can be built from source but it's not required because all the needed binaries (including FitNesse) are available.

The *bin* directory in the project root contains SmartHomeTests.exe which provides the testing interface of the SUT. When executed, it starts listening for http connections from FitNesse. The *FitNesse* directory (also in the root) contains FitNesse, easyfit and all their dependencies. So, to run some tests

1. [download](https://github.com/nikoudel/SmartHome/archive/master.zip) and extract the SmartHome project somewhere
2. run it by executing SmartHome\bin\SmartHomeTests.exe
3. start fitnesse by running SmartHome\FitNesse\start.bat
4. point your browser to the [TurnKitchenLightsOnOff][lights] test and press the Test button

**Note:** step 3 requires [Java JRE](http://www.oracle.com/technetwork/java/javase/downloads/index.html) to be installed and java.exe to be added to PATH environment variable. Alternatively you can modify start.bat to point to java.exe with absolute path, for example like this:

    "C:\Program Files\Java\jre7\bin\java.exe" -jar fitnesse-standalone.jar -p 8080

The test mentioned above should pass. It tests an imaginary [home automation](http://en.wikipedia.org/wiki/Home_automation) system which lets the user operate and monitor house devices (eg. lights, coffee maker, dish washer etc.) over network with, let's say, a smart phone. In particular, [TurnKitchenLightsOnOff][lights] tests that kitchen lights turn on and off as expected. However, before the test gets to a point where it can turn the lights on and off, a lot of things needs to happen, so let's start from the beginning.

After having started FitNesse point your browser to [http://localhost:8080/SmartHome](http://localhost:8080/SmartHome):

![](https://raw.github.com/nikoudel/easyfit/images/SmartHome.PNG "SmartHome main page")

This is the root page of SmartHome test suite in FitNesse. It has two tests below it (SaunaSchedule and TurnKitchenLightsOnOff), the SuiteSetUp page and a couple of configuration lines.

The TEST_SYSTEM variable defines [Slim](http://fitnesse.org/FitNesse.UserGuide.SliM) test system to be enabled everywhere below the [SmartHome](http://localhost:8080/SmartHome) page. Slim test system gives access to the [Table](http://fitnesse.org/FitNesse.UserGuide.SliM.TableTable) FitNesse table which is the basement easyfit is built on top of.

The classpath definitions add easyfit dependencies:

<dl>
	<dt>.\lib</dt>
	<dd>adds the lib directory to the classpath (needed for accessing the logging configuration file log4j2.xml)</dd>
	<dt>.\lib\scala-library.jar</dt>
	<dd>adds the <a href="http://www.scala-lang.org">Scala</a> library (easyfit is written in Scala)</dd>
	<dt>.\lib\easyfit_2.10-0.1.jar</dt>
	<dd>adds the easyfit library itself</dd>
	<dt>.\lib\log4j-api-2.0-beta9.jar</dt>
	<dd>part of log4j logging utility</dd>
	<dt>.\lib\log4j-core-2.0-beta9.jar</dt>
	<dd>part of log4j logging utility</dd>
	<dt>.\lib\filters_2.10-1.0.jar</dt>
	<dd>optional filtering library (will be explained later on this page)</dd>
</dl>

SuiteSetUp is a [set-up](http://fitnesse.org/FitNesse.UserGuide.TestSuites.SuiteSetUpAndSuiteTearDown) page which prepares the initial state for all tests in a suite. It gets executed automatically upon running a test (before the test). When running the whole suite (a collection of tests), SuiteSetUp gets executed only once.

In this case [http://localhost:8080/SmartHome.SuiteSetUp](http://localhost:8080/SmartHome.SuiteSetUp) initialized the SUT by creating domain objects, namely rooms and devices:

![](https://raw.github.com/nikoudel/easyfit/images/SuiteSetup.PNG ".SmartHome.SuiteSetUp")

First, the [easyfit.tables](https://github.com/nikoudel/easyfit/tree/master/src/main/scala/easyfit/tables) namespace is [imported](http://fitnesse.org/FitNesse.UserGuide.SliM.ImportTable). Now the tables can be accessed with a shorter name (Table:Row instead of Table:easyfit.tables.Row).

Second, a URL of System Under Test is configured. With this configuration SUT is expected to listen on http port 56473 and have a suffix ".json". The "(controller)" part will be replaced with a table action, eg. http://localhost:56473/CreateRoom.json.

Third, filter "tf" is defined. Filters help to overcome possible JSON serialization issues. Filters will be explained later on this page.

Finally, the SUT call: SuiteSetUp includes three Row tables (CreateOtherDevices is similar to the other two and is collapsed to save screen space).

Row table
---------

Every table has a name (Row in this case). Row and Query tables have SUT action (CreateRoom) and a header row. Row table has at least one row. In this case it has three rows, so the SUT will be called three times.

![](https://raw.github.com/nikoudel/easyfit/images/RowTable.png "Row table")

Table name tells FitNesse which table to create. Easyfit Row table is in [Row.scala](https://github.com/nikoudel/easyfit/blob/master/src/main/scala/easyfit/tables/Row.scala) file.

SUT action tells the table which operation to call on the SUT side. The following code sample demonstrates easyfit's entry point into the SUT which is implemented using the [ServiceStack][ss] communication framework (an alternative to Microsoft WCF).

CreateRoom action leads to CreateRoomService.Post method in [CreateRoomService.cs](https://github.com/nikoudel/SmartHome/blob/master/SmartHomeTests/Services/CreateRoomService.cs):

```csharp
/// <summary>
/// An http POST to /CreateRoom URL will be serialized as RoomData and passed to
/// CreateRoomService.Post method which returns a RoomData object to be serialized
/// and sent back to the caller over http.
/// </summary>
[Route("/CreateRoom", "POST")]
public class RoomData
{
    public long Id { get; set; }
    public RoomType RoomType { get; set; }
}

public class CreateRoomService : Service
{
    //Injected by IOC (see ApplictionHost.Configure)
    public Repository<Room> Repository { get; set; }

    /// <summary>
    /// Create and add a new Room into the repository.
    /// </summary>
    public RoomData Post(RoomData data)
    {
        // Note: repository creates a Room, not RoomData.
        var room = Repository.Store(Room.createInstance(data.RoomType));

        // set room ID back to the data object
        data.Id = room.Id;

        return data;
    }
}
```

The CreateRoomService.Post method expects a RoomData item as an input parameter. The fields of RoomData (Id and RoomType) are provided in the header row of the Row table. It also returns a RoomData back to FitNesse.

**Note:** SmartHome\FitNesse\lib directory has a log4j2.xml file in it. By default, the file configures log4j to print all the data passing between easyfit and the SUT into the SmartHome\FitNesse\easyfit.log log file. In case of the Row table mentioned above, the data would look like this:

    ... POST http://localhost:56473/CreateRoom.json: {"Id":null,"RoomType":"Kitchen"} -> {"Id":1,"RoomType":"Kitchen"}
    ... POST http://localhost:56473/CreateRoom.json: {"Id":null,"RoomType":"Sauna"} -> {"Id":2,"RoomType":"Sauna"}
    ... POST http://localhost:56473/CreateRoom.json: {"Id":null,"RoomType":"LivingRoom"} -> {"Id":3,"RoomType":"LivingRoom"}

The log shows that there were three POST calls to http://localhost:56473/CreateRoom.json with incoming data on the left side of the arrow and response on the right.

The Id field is null when entering the SUT because the Row table is about to initialize variables $kitchen, $sauna and $livingRoom with values coming from the SUT (1, 2 and 3). This mechanism is similar to FitNesse [symbols](http://localhost:8080/FitNesse.UserGuide.SliM.SymbolsInTables) but it's fully re-implemented in easyfit allowing, for example, variable initialization inside the Query table.

After having executed the [SuiteSetUp](http://localhost:8080/SmartHome.SuiteSetUp) it looks like this:

![](https://raw.github.com/nikoudel/easyfit/images/SuiteSetupExecuted.PNG "SuiteSetUp executed")

The left-side arrow indicates variable initialization and eg. $kitchen [1] means a variable with value 1 has been used. The data of CreateDevice row table follows:

    ... POST http://localhost:56473/CreateDevice.json: {"Id":null,"RoomId":"1","DeviceType":"CeilingLight"} -> {"Id":1,"RoomId":1,"DeviceType":"CeilingLight","DeviceState":"Undefined"}
    ... POST http://localhost:56473/CreateDevice.json: {"Id":null,"RoomId":"1","DeviceType":"LedSpotLight"} -> {"Id":2,"RoomId":1,"DeviceType":"LedSpotLight","DeviceState":"Undefined"}
    ... POST http://localhost:56473/CreateDevice.json: {"Id":null,"RoomId":"1","DeviceType":"LedSpotLight"} -> {"Id":3,"RoomId":1,"DeviceType":"LedSpotLight","DeviceState":"Undefined"}
    ... POST http://localhost:56473/CreateDevice.json: {"Id":null,"RoomId":"1","DeviceType":"LedSpotLight"} -> {"Id":4,"RoomId":1,"DeviceType":"LedSpotLight","DeviceState":"Undefined"}
    ... POST http://localhost:56473/CreateDevice.json: {"Id":null,"RoomId":"2","DeviceType":"SteamLight"} -> {"Id":5,"RoomId":2,"DeviceType":"SteamLight","DeviceState":"Undefined"}
    ... POST http://localhost:56473/CreateDevice.json: {"Id":null,"RoomId":"3","DeviceType":"CeilingLight"} -> {"Id":6,"RoomId":3,"DeviceType":"CeilingLight","DeviceState":"Undefined"}
    ... POST http://localhost:56473/CreateDevice.json: {"Id":null,"RoomId":"3","DeviceType":"TableLight"} -> {"Id":7,"RoomId":3,"DeviceType":"TableLight","DeviceState":"Undefined"}

Query table
-----------
After the SuiteSetUp, the actual test ([http://localhost:8080/SmartHome.TurnKitchenLightsOnOff](http://localhost:8080/SmartHome.TurnKitchenLightsOnOff)) begins:

![](https://raw.github.com/nikoudel/easyfit/images/TurnLightsTest.PNG "TurnKitchenLightsOnOff (part 1)")

The first table turns kitchen lights on. The code handling device state change is in [ChangeDeviceStateService.cs](https://github.com/nikoudel/SmartHome/blob/master/SmartHomeTests/Services/ChangeDeviceStateService.cs).

Next, the state change is verified using the Query table. However, before calling the query table, the search criteria need to be defined. In this case, only kitchen lights are needed. That's what easyfit [Map](https://github.com/nikoudel/easyfit/blob/master/src/main/scala/easyfit/tables/Row.scala) table is for: it defines a list of key-value pairs (only one here, RoomId = $kitchen) and gives the list an identifier ($query) which is used to link the criteria to the Query table.

Just as Row table, Query table has a header row defining the field names (Id and DeviceState) requested from the SUT. [DeviceService.cs](https://github.com/nikoudel/SmartHome/blob/master/SmartHomeTests/Services/DeviceService.cs) handles both device creation (POST) and querying (GET):

```csharp
/// <summary>
/// An http POST to /CreateDevice URL will be serialized as DeviceData and passed to
/// DeviceService.Post method which returns a DeviceData object to be serialized
/// and sent back to the caller over http.
/// 
/// An http GET to /GetDevices URL will be serialized as DeviceData and passed to
/// DeviceService.Get method which returns a DeviceData object to be serialized
/// and sent back to the caller over http.
/// </summary>
[Route("/CreateDevice", "POST")]
[Route("/GetDevices", "GET")]
public class DeviceData : IRepositoryItem
{
    public long Id { get; set; }
    public long RoomId { get; set; }
    public DeviceType DeviceType { get; set; }
    public DeviceState DeviceState { get; set; }
}

public class DeviceService : Service
{
    //Injected by IOC (see ApplictionHost.Configure)
    public Repository<DeviceData> Repository { get; set; } 

    /// <summary>
    /// Add a new DeviceData into the repository.
    /// </summary>
    public DeviceData Post(DeviceData item)
    {
        return Repository.Store(item);
    }

    /// <summary>
    /// Get a list of devices based on a given criteria.
    /// </summary>
    public List<DeviceData> Get(DeviceData crit)
    {
        var query = from device in Repository.Items select device;

        if(crit.Id > 0)
        {
            query = query.Where(device => device.Id == crit.Id);
        }

        if(crit.RoomId > 0)
        {
            query = query.Where(device => device.RoomId == crit.RoomId);
        }
        
        if(crit.DeviceType != DeviceType.Undefined)
        {
            query = query.Where(device => device.DeviceType == crit.DeviceType);
        }

        if (crit.DeviceState != DeviceState.Undefined)
        {
            query = query.Where(device => device.DeviceState == crit.DeviceState);
        }

        return query.ToList<DeviceData>();
    }
}
```

Query table's data flow is different from the Row's:

    ... GET http://localhost:56473/GetDevices.json?RoomId=1: [{"Id":1,"RoomId":1,"DeviceType":"CeilingLight","DeviceState":"On"},{"Id":2,"RoomId":1,"DeviceType":"LedSpotLight","DeviceState":"On"},{"Id":3,"RoomId":1,"DeviceType":"LedSpotLight","DeviceState":"On"},{"Id":4,"RoomId":1,"DeviceType":"LedSpotLight","DeviceState":"On"}]

There is only one GET call having RoomId=1 as an argument and returning four items.

Next, two lights are switched off (step 3) and the new state is inspected (step 4) with the same query parameter (RoomId=1):

![](https://raw.github.com/nikoudel/easyfit/images/TurnLightsTest2.PNG "TurnKitchenLightsOnOff (part 2)")

In step 5, the $query is re-defined to include two criterions, RoomId = $kitchen and DeviceState = On. The data contains now two arguments and returns two rows:

    ... GET http://localhost:56473/GetDevices.json?RoomId=1&DeviceState=On: [{"Id":2,"RoomId":1,"DeviceType":"LedSpotLight","DeviceState":"On"},{"Id":3,"RoomId":1,"DeviceType":"LedSpotLight","DeviceState":"On"}]

The ending part of [TurnKitchenLightsOnOff](http://localhost:8080/SmartHome.TurnKitchenLightsOnOff) looks like this when executed:

![](https://raw.github.com/nikoudel/easyfit/images/TurnLightsExecuted.PNG "TurnKitchenLightsOnOff executed")

Other features
--------------

So far easyfit tables were used only to modify or inspect persistent SUT state (ie. whether an item exists or not). The second test, [http://localhost:8080/SmartHome.SaunaSchedule](http://localhost:8080/SmartHome.SaunaSchedule), tests logical state. First (step 1), it configures a sauna to turn on and off at certain time of the day, two days a week:

![](https://raw.github.com/nikoudel/easyfit/images/SaunaTest.PNG "SaunaSchedule test")

Next (step 2), GetSaunaState Row table tests if the state matched the expected values at different points in time. This example has a couple of new features: some column names end with "?" sign, the DateTime column includes the "tf" filter and some cells have an empty value in them. To describe these features, let's look at the raw data once again:

    ... POST http://localhost:56473/GetSaunaState.json: {"Sauna":"2","DateTime":"2013-09-24 18:29","Stove":null,"Light":null,"Temperature":null} -> {"Sauna":2,"DateTime":"\/Date(1380036540000+0300)\/","Stove":"Off","Light":"Off","Temperature":22}
    ... POST http://localhost:56473/GetSaunaState.json: {"Sauna":"2","DateTime":"2013-09-24 18:30","Stove":null,"Light":null,"Temperature":null} -> {"Sauna":2,"DateTime":"\/Date(1380036600000+0300)\/","Stove":"On","Light":"Off","Temperature":22}
    ... POST http://localhost:56473/GetSaunaState.json: {"Sauna":"2","DateTime":"2013-09-24 18:45","Stove":null,"Light":null,"Temperature":null} -> {"Sauna":2,"DateTime":"\/Date(1380037500000+0300)\/","Stove":"On","Light":"Off","Temperature":56}
    ... POST http://localhost:56473/GetSaunaState.json: {"Sauna":"2","DateTime":"2013-09-24 20:30","Stove":null,"Light":null,"Temperature":null} -> {"Sauna":2,"DateTime":"\/Date(1380043800000+0300)\/","Stove":"Off","Light":"Off","Temperature":90}
    ... POST http://localhost:56473/GetSaunaState.json: {"Sauna":"2","DateTime":"2013-09-24 21:00","Stove":null,"Light":null,"Temperature":null} -> {"Sauna":2,"DateTime":"\/Date(1380045600000+0300)\/","Stove":"Off","Light":"Off","Temperature":22}
    ... POST http://localhost:56473/GetSaunaState.json: {"Sauna":"2","DateTime":"2013-09-25 19:30","Stove":null,"Light":null,"Temperature":null} -> {"Sauna":2,"DateTime":"\/Date(1380126600000+0300)\/","Stove":"Off","Light":"Off","Temperature":22}
    ... POST http://localhost:56473/GetSaunaState.json: {"Sauna":"2","DateTime":"2013-09-27 19:20","Stove":null,"Light":null,"Temperature":null} -> {"Sauna":2,"DateTime":"\/Date(1380298800000+0300)\/","Stove":"On","Light":"On","Temperature":68}
    ... POST http://localhost:56473/GetSaunaState.json: {"Sauna":"2","DateTime":"2013-09-27 21:00","Stove":null,"Light":null,"Temperature":null} -> {"Sauna":2,"DateTime":"\/Date(1380304800000+0300)\/","Stove":"Off","Light":"Off","Temperature":90}

### One-way data flow with "?" sign

The "?" sign defines a column with one-way data flow, ie. although a cell has some non-empty value (eg. Temperature = 22), the data log shows that there is no value being passed from FitNesse to SUT ("Temperature":null). It only comes back from SUT ("Temperature":22). Although in this particular case (and in vast majority of all cases in general) it's irrelevant whether there is a value going into SUT or not (see [GetSaunaStateService.cs](https://github.com/nikoudel/SmartHome/blob/master/SmartHomeTests/Services/GetSaunaStateService.cs)), sometimes values coming into SUT are expected to be null. "?" sign provides such a possibility.

### Filters

The "tf" filter defined earlier in the [SuiteSetUp](http://localhost:8080/SmartHome.SuiteSetUp) takes care of the date serialization problem which can be noticed from the data log. ServiceStack serializes dates in the "milliseconds since epoch" format, eg. "/Date(1380304800000+0300)/". FitNesse test, however, expects the date to be in a custom format, eg. "2013-09-27 19:20". The actual data coming from the SUT needs to be converted into a human readable form before being matched to the expected values.

Easyfit handles the problem by supporting custom "filters" which can modify data values leaving FitNesse ("expected" values) and coming back in ("actual" values). Expected and actual values are processed by filters defined in **ExpectedFilter** and **ActualFilter** tables, respectively. See [Filter.scala](https://github.com/nikoudel/easyfit/blob/master/src/main/scala/easyfit/tables/Filter.scala) for implementation details.

In this case the data requiring modification originates from SUT, so an ActualFilter needs to be defined:

![](https://raw.github.com/nikoudel/easyfit/images/ActualFilter.PNG "filter definition")

Filter definition tables have no header row, just two columns: filter alias to be used in tests on the left side of the column name (eg. "tf" in "tf:DateTime") and full name of the class implementing the filter (eg. "filters.TimeFormatter"). Easyfit loads the class with Java reflection as long as it can be found from the classpath. The .\lib\filters_2.10-1.0.jar definition on the [root page](http://localhost:8080/SmartHome) adds the library containing the filter class (filters.TimeFormatter) to the classpath.

A sample filter project is available [here] (https://github.com/nikoudel/filters). A filter method needs to implement the [IFilter](https://github.com/nikoudel/easyfit/blob/master/src/main/java/easyfit/IFilter.java) interface defined in easyfit:

```java
package easyfit;

/**
 * The interface to be implemented by filter classes.
 */
public interface IFilter
{
    public String apply(String value);
}
```

The [implementation](https://github.com/nikoudel/filters/blob/master/TimeFormatter.java) takes a string in and returns a modified string back:

```java
package filters;

import easyfit.IFilter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeFormatter implements IFilter
{
    @Override
    public String apply(String s)
    {
        try
        {
            //e.g. /Date(1380036540000+0300)/
            Pattern p = Pattern.compile("^/Date\\((\\d+)\\+\\d+\\)/$");
            Matcher m = p.matcher(s);

            if (!m.matches()) return s;

            Date date = new Date(Long.parseLong(m.group(1)));

            return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
        }
        catch(Exception ex)
        {
            return ex.getMessage();
        }
    }
}
```

Now the cells of DateTime column (prefixed with "tf") apply the filter to the values, converting eg. "/Date(1380036540000+0300)/" to "2013-09-24 18:29" before matching to the expected values.

### Empty cell values

The first four cells in the Light column are empty. Although the SUT returns some non-empty values in these cells, the test doesn't fail. Empty cells can be used for visualizing SUT state before making a decision about correct expected values. The [SaunaSchedule](http://localhost:8080/SmartHome.SaunaSchedule) test looks like this when executed:

![](https://raw.github.com/nikoudel/easyfit/images/SaunaTestExecuted.PNG "SaunaSchedule executed")

It has one failed value resulting from a fault in the SUT domain [logic](https://github.com/nikoudel/SmartHome/blob/master/SmartHomeDomain/Sauna.cs) but it's out of scope of this demo to fix it :)

### Debugging

Debugging SmartHome is easy - just install Visual Studio 2013 Express Edition (it includes NuGet which is required to get ServiceStack libraries), set StartHomeTests as StartUp project and hit F5. Breakpoints can be set in Get and Post methods of the service classes.

Debugging easyfit is a bit harder, because it's executed by FitNesse. An easier solution would probably be to add log4j logging to needed places (see [HttpConnection.java](https://github.com/nikoudel/easyfit/blob/master/src/main/java/easyfit/HttpConnection.java) for an example). However, easyfit didn't require any debugging during development because it's covered with unit tests pretty well (see the [easyfit.test](https://github.com/nikoudel/easyfit/tree/master/src/test/scala/easyfit.test) package).

To build easyfit and run the tests:

1. install [Scala](http://www.scala-lang.org/download)
2. install [SBT](http://www.scala-sbt.org) and add "C:\Program Files (x86)\sbt\bin" to the PATH
3. clone easyfit (git clone https://github.com/nikoudel/easyfit.git)
4. update the minimal-json submodule ("git submodule init" and "git submodule update")
5. go to the easyfit root folder where build.sbt is and run "sbt test"

The output should look like this:

![](https://raw.github.com/nikoudel/easyfit/images/sbt_test.PNG "easyfit tests")

*Note:* "sbt package" creates a new easyfit jar file in the target\scala-2.10 directory

That's all for now! Please don't hesitate to contact the author if you have any questions or report an issue if you find one.

[lights]: http://localhost:8080/SmartHome.TurnKitchenLightsOnOff
[ss]: https://servicestack.net