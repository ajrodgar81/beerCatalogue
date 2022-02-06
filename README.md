# Beer Catalogue

## Rest API Demo

**Technical details:**


- Based on Java version: 11, Spring boot: 2.6.3 and open-api 3.0.


- Use h2 in memory database to facilitate the deploy and execution. It uses the default login: username = sa, no password.


- Following clean code techniques: short methods, good naming, SOLID principles...


- Reduce generating code using libraries like Lombok, framework validation constraints...


- Smart testing: by example data classes are tested indirectly. Server validations constraints tests, a hierarchy test data, specific sort pagination tests and wrapped body response test classes were created as well.


- Decouple persistence layer using DTO pattern. Useful if we want to use in the future another kind of repository.


- Error handling: design application exception hierarchy, add custom error messages and provide a custom error handling response.


- From API perspective, to keep as much as possible the solid principle Single Responsibility, among others, I "broke" the relation one to many (Beer to Manufacturer) into many to many, in other words, they are considered as two "independent" resources. This way we have two separates controllers, Manufacturers and Beers, who handle the life cycle of manufacturers and beer resources respectively.


- Delete operation, I implement soft delete instead of hard delete mechanism. Data are very important and we could use those data to provide new products or improve the current one (data mining...). The current api implementation exclude from the query results the resources with the flag delete set to true.


- API Documentation using open-api 3.0 and swagger ui.

<ul>
	<li>Sort pagination for single and multiple fields in the collections type. Sort format:</li><br>
	<ul>
		<li>Single field: sort=fieldName&sort=[direction]</li><br>
		<li>Multiple fields: {sort=fieldName,[direction]}&{sort=fieldName2,[direction2]}*<br><br>where direction € [asc, desc]</li>
	</ul>
</ul>

- History commits: it reflects the normal development process: start developing a basic solution, enhanced, last minute changes and fix it after the code review before delivery the feature. For example, in the code review phase I detect that a class of junit4 library was imported by mistake when I migrate the tests to Jupiter (Junit version 5).<br>

- Some code statistics: 184 tests, global coverage 81,1 %.<br><br>


**Business logic:**


- A beer can have registered only one manufacturer


- A manufacturer can have multiple beers


- Delete a manufacturer causes that all beers associated to him will be marked as deleted

<ul>
	<li>To keep the data integrity, the following operations are not allowed and they will cause an error response:</li><br>
	<ul>
		<li>Create a new manufacturer when the new identifier is provided</li><br>
		<li>Get the manufacturer details when the manufacturer not exists</li><br>
		<li>Get the manufacturer beers when the manufacturer not exists</li><br>
		<li>Modify the id of the existing manufacturer</li><br>
		<li>Delete a not existing manufacturer</li><br>
		<li>Create a new manufacturer when the new identifier is provided</li><br>
		<li>Create a beer when its manufacturer not exists</li><br>
		<li>Get the beer details when the beer not exists</li><br>
		<li>Modify the id of the existing beer</li><br>
		<li>Modify the manufacturer of the existing beer</li><br>
		<li>Delete a not existing beer</li><br>
		<li>In sort pagination operations, not provided a valid sort criteria format described before.</li><br>
	</ul>
</ul>

<ul>
	<li>Assuming that a local instance of the API is running:</li><br>  
		<ul>
			<li>[Beer Catalogue API Docs](http://localhost:8080/beerCatalogue/api/v3/api-docs)</li><br>
			<li>[Beer Catalogue API Swagger UI] (http://localhost:8080/beerCatalogue/api/swagger-ui/index.html)</li><br>
			<li>[H2 Console] (http://localhost:8080/beerCatalogue/api/h2-ui/)</li><br> 
		</ul>
	<li>Alternatively, you could generate the api specification from a command line using the following maven command: "mvn clean verify -DskipTests=true". The result could be found in the project folder "/target/openapi-spec".</li><br>
</ul>

**Installation:**

<ol>
	<li>Open a terminal console in the project root folder and execute the following command to generate the jar: mvn -U clean install</li><br>
	<li>On the same terminal console, change the directory to the generated project folder "target"</li><br>
	<li>In the project folder "target", to run the application execute the following command: java -jar beerCatalogue-0.0.1-SNAPSHOT.jar</li><br>  
</ol>

**TODO for the next springs:**

- Add logs and actuator end-points<br>


- Secure the api<br>


- Enable search by the pending beer fields: name, graduation, type, description<br>
...
