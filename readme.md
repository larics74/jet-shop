# JetShop Application (Spring Boot + Thymeleaf)

The main purpose was to learn some Spring/Spring Boot features (MVC, Spring Data, Security, AOP etc.). UI is created with Thymeleaf (no JavaScript, pure CSS). In-memory database H2 is used for data storage.

## Running the app

As a jar file:

`java -jar target/jetshop-0.0.1-SNAPSHOT.jar`


Using Maven:

`mvn spring-boot:run`

## Features

JetShop application allows to manage Jet orders.
The users can do the following actions (depending on role):

- register/log in/view profile/edit profile/log out
- view/create/edit/delete any user profile (ADMIN only)
- view Jet models
- create/edit/delete Jet models (ADMIN only)
- view/create/edit Jet orders
- view/create/edit/delete Jet orders for any customer (ADMIN only)
- view Jet factory schedule page (under construction)
- view design page, which represents overall UI design in one page (ADMIN only)
- select UI language (English/Russian)
- view help page