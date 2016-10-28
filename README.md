#Location-Based Project Through Android System
Background: 
Nowadays people take photos with their cell phone and we could use the metadata to perform some interesting analysis based on the timestamp and GPS data. The project's aim is to test the efficiency of Realm Database and Android original SQLite Database utilizing the location and timestamp information retrieved from the photos.

For those who are not familiar with Realm Database, please refer to : https://realm.io/docs/java/latest/

The Realm based version is build upone Realm 1.0.0

###The App's Function:

1. Group the photos according to the timeline

2. Cluster the photos' location on map

3. Perform Query Testing&Evaluation for Realm and SQLite

### The Interface of the App:

![pjimage](https://cloud.githubusercontent.com/assets/13210944/17783588/8ff0c6ca-652d-11e6-8a79-b52bf0c3213c.jpg)


### How to Use and Install:
  By default, the app will require both the photos under the Pictures folder and internet connection. Thus if you are running the app on a real device, make sure that your device have pictures under the Pictures folder and you either turn on wifi or cellular data before running the app, else you will receive the notification that your photos are not loaded correctly. If you use the emulator instead, use the adb push command to push your photos of .jpg to the emulator. 

### The structure of the app (Realm & SQLite)

#### 1. Realm
Project Name: RealmPractice

-map--(package): contains the class files for map cluster

-model--(package): Photo.class defines the database table; GeoLocation.class is used to return the bounding box cordinates in finding the nearest photo test

Constants.class: store the constant used to pass between intent service

FetchAddress.class: retrieve the geo information and insert the data into the database

OptionChooser.class: the interface that choose show timeline or show photo's location on map

ShowTimeline.class: show photos in time order

TestQuerySpeed: implemented in background service, perform all the tests related to the database

#### 2. SQLite
Project Name: SqlitePhoto

--model(package): Constant.class used to store the string that define the database and used to pass between service; Photo.class defines the database object 

DBHandler: deal with the main database operation

FetchAddress: retrieve the geo information from the photos and insert into the database

GeoLocation: the bounding box used in nearest location test that returns the four coordinates

TestQuerySpeed: perform the tests related to database

###The DataBase Table Defined:

  For both Realm and SQLite, the database is defined as below, revealing the GPS(Latitude&Longitude),TimeStamp and Location for each photo. With the photo's name being its id. For both Realm and SQLite version , you can find the database description file located under "model" package, the file "Photo.class". 

![screen shot 2016-08-17 at 3 58 44 pm](https://cloud.githubusercontent.com/assets/13210944/17756238/9146c3d4-6493-11e6-9d12-cb62ec99b43a.png)

### Load the data to the Database

  By clicking the button "Load Photo", you start to insert the data. The process of retriving the exif data(GPS and timestamp) of the photos is implemented in background intent service: FetchAddress.class. Insert the data to the table and in the meantime record the time elapse during the insert process. 

### Query Speed Comparison Between SQLite&Realm

   Implemented in background service "TestQuerySpeed". Perform three kind of test query:

- Equal Query: Select from the database the photos whose location is "Evanston" for 1000 times
- Select Query: Select the photo within a random month range or date range, each perform 1000 times 
- Finding the Nearest Location: Select the photos closest to a random location in the database. Run the test for 5000 times. 

  Algorithms: Instead of simply calculate all the distance to a point and select the smallest among the candidate point, first compute a bounding box coordinates that can be used for a database index scan – just like we would use minimum bounding rectangles to speed up queries in Cartesian space. The speed is improved when narrow down the candidates within the bounding box and then perform the distance calculations and iteration to decide the nearest photo location in the databases. Perform the same algorithms on SQLite and Realm

  ####Test Result:
  Based on the device: HuaWei Nexus 6P 64GB RAM:3GB
  
![untitled](https://cloud.githubusercontent.com/assets/13210944/18603251/b9fc6cb8-7c24-11e6-87d6-f5fda2b8c333.png)

  ![image001](https://cloud.githubusercontent.com/assets/13210944/17864963/184e9a0e-6855-11e6-9bee-98a34bf8cbfe.png)
  
### Comparsion Of Disk Space& Memory Use 
  
  For 1000 items in the database, the Realm takes 360 KB while the SQLite takes 94 KB.
  The memory usage is similar between Realm and SQLite

### Scalibility

  As database's size grows, the realm's speed over SQLite keeps almost the same and slight reduced when the photo album's size grows to 2000. 
  
  ![image002](https://cloud.githubusercontent.com/assets/13210944/18457038/6a790aec-790a-11e6-8838-15cf8d75cd63.png)

### Conclusions

As can be seen from the chart, for all the 4 query type testing, Realm is much more faster than the traditional SQLite database when dealing with the GIS data from the photos. Realm is also easier to use. SQLite requires lots of Strings to define the table and the thread management could lead to hard-to-debug exceptions.The problem with Realm is that that it doesn't support some specific query like calendar oriented function which are supported in traditional SQLite database. Therefore, if the app requires faster speed then Realm could be a good choice over SQLite, though many other aspects may need consideration.





