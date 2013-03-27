Scrattle Alpha
Mathew Gray

If you want to host it on a machine of your choice, here's what you do
(I'm sorry this is so manual, I didn't have time to spring configure it yet):

Before you package it, go to the top of the GameServiceImpl file in the server
package and change the static string SERVER to match the host and port
(eg. http://127.0.0.1:8888/)

Also, if you're not hosting your mysql server on localhost, you'll have to
change the host in PersistanceService (also in server package) where it says:

        connect = DriverManager
                .getConnection("jdbc:mysql://localhost/SCRABBLE?"
                        + "user=app&password=apppw");

Here's the sql commands you'll need to run to setup the database/tables:

CREATE DATABASE SCRABBLE;
CREATE USER app IDENTIFIED BY 'apppw';
grant usage on *.* to app@localhost identified by 'apppw';
grant all privileges on SCRABBLE.* to app@localhost;
CREATE TABLE GAME (id INT NOT NULL AUTO_INCREMENT, NAME VARCHAR(50) NOT NULL,DATA MEDIUMBLOB, LAST_UPDATE TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, PRIMARY KEY(ID));
CREATE TABLE LEADERBOARD (DEPT VARCHAR(70) NOT NULL, SCORE INT NOT NULL,
PRIMARY KEY (DEPT));

Now run 'mvn package' to compile and package the webapp into a war file
(it'll output into a folder called target).  You can then deploy this to
tomcat or jetty.

Some other useful maven commands:

mvn clean # delete temporary stuff
mvn test # run all the tests (gwt and junit)
mvn gwt:run # run development mode
mvn gwt:compile # compile to javascript
mvn package # generate a .war package ready to deploy