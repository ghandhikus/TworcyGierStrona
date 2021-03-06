# Website Twórcy Gier

## Project structure
* [Source](src) - All project files, excluding maven and git configuration.
 * [Tests](src/test) - Test module, which contains all test-only files.
 * [Main](src/main) - Contains project files.
    * ![Gear img](https://pubs.vmware.com/vrealizeoperationsmanager-6/topic/com.vmware.vcom.core.doc/GUID-67A2F2E1-67AA-447F-B01B-71CAC8C4DE4D-low.png)
      [Resources](src/main/resources) - Stores property files, language files, and minor settings.
    * ![Front-End img](http://www.cakescookiesandcraftsshop.co.uk/skin/frontend/base/default/images/rewards/add_points.png)
      [Web Application](src/main/webapp) - Holds all frontend files.
      * [META-INF](src/main/webapp/META-INF) - Contains MANIFEST.MF. Required by Maven.
      * [Resources](src/main/webapp/resources) - Holds all resources for frontend, js, images, css, fonts.
      * [WEB-INF](src/main/webapp/WEB-INF) - All server-side front-end files.
        * [JSP](src/main/webapp/WEB-INF/jsp) - All JSP files. JSPs are mainly used for generating the html files in this project.
        * [Libraries](src/main/webapp/WEB-INF/lib) - Maven cache folder, for libraries. Excluded in .gitignore
    * ![Back-End img](http://icons.iconarchive.com/icons/led24.de/led/16/page-code-icon.png)
      [Java](src/main/java) - Holds all backend code.
      * [Tworcy](src/main/java/com/clockwise/tworcy) - MVC module for backend.

## Fronend Libraries used
Please browse [Frontend Resources](src/main/webapp/resources) for more

* [jQuery](https://jquery.com/)
* [jQuery colors](https://github.com/jquery/jquery-color)
* [jQuery UI](https://jqueryui.com/)
* [Font Awesome](https://fortawesome.github.io/Font-Awesome/)
* [Bootstrap](http://getbootstrap.com/)
* ~~[angular.js](https://angularjs.org/)~~ not yet, but it is a next thing in TODO
* ~~[angular-ui bootstrap](https://github.com/angular-ui/bootstrap)~~
* [SCEditor](http://www.sceditor.com/)
* [Masonry](http://masonry.desandro.com)

## Backend Libraries used
For full list see [pom.xml](https://github.com/ghandhikus/TworcyGierStrona/blob/master/pom.xml)

* [Log4j](http://logging.apache.org/log4j)
* [Apache Commons](https://commons.apache.org/)
* [JNA 4](https://github.com/java-native-access/jna)
* [JUnits 4](http://junit.org/junit4/)
* [Joda Time](http://www.joda.org/joda-time/)
* [Jadira](http://jadira.sourceforge.net/) (for Joda Time and Hibernate 4 integration)
* [Jodd](http://jodd.org/)
* [Gson](https://github.com/google/gson)
* [Jsoup](http://jsoup.org/)
* [JSTL](http://www.oracle.com/technetwork/java/index-jsp-135995.html)
* [Kefirbb](http://kefirsf.org/kefirbb/) (bbcode)
* [MySQL Connector](https://www.mysql.com/products/connector/)
* [Hibernate 4](http://hibernate.org/orm/downloads/)
* [Spring Framework](http://projects.spring.io/spring-framework/)
* [Spring Security](http://projects.spring.io/spring-security/)
