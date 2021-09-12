
Run Application Using Maven Commands:

Note: maven should be locally installed and path set in system environment variables for path

go to project location and do the following

1. Start the Spring Boot Server <br/>
``mvn spring-boot:run``

make sure your are inside the project where pom.xml exists.

2. Start the Spring Boot with Custom profile <br/>
``mvn spring-boot:run -Dspring-boot.run.profile=dev``

here dev is id of profile you want to run <br/>
if you don't provide anything blank will be passed will run application.properties

3. To enable profile add this in pom.xml

```
		<profiles>
			<profile> 
			<id>dev</id>
			<properties>
			<activatedProperties>dev</activatedProperties>
			</properties> 
			</profile>
		<profile>
		<!-- other profile>
		</profile>
		</profiles>
```
4. Run Unit Tests <br/>
	``mvn test``

5. Run only Integrated Tests <br/>
	``mvn failsafe:integration-test``<br/> <br/>
	any test files ending with **IT.java will be treated as integration test files
	<br/>
	<br/>
6. plugin for run integration tests

```
<plugin>
	<groupId>org.apache.maven.plugins</groupId>
	<artifactId>maven-failsafe-plugin</artifacId>
	<executions>
		<execution>
			<id>integration-tests</id>
			<goals>
				<goal>integration-test</goal>
				<goal>verify</goal>
			</goals>
		</execution>
	</executions>
	<dependencies>
                <dependency>
                    <groupId>org.junit.jupiter</groupId>
                    <artifactId>junit-jupiter-engine</artifactId>
                    <version>5.3.2</version>
                </dependency>
            </dependencies>
</plugin>
```


