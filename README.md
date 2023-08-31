[![License](https://img.shields.io/github/license/hoomb/json-db-update.svg)](https://raw.githubusercontent.com/hoomb/json-db-update/master/LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/de.hoomit.projects/json-db-update.svg)](https://central.sonatype.com/artifact/de.hoomit.projectsjson-db-update/)
[![JavaDoc](http://javadoc.io/badge/de.hoomit.projectsjson-db-update.svg)](http://www.javadoc.io/doc/de.hoomit.projectsjson-db-update)

# Introduction

Storing relationships as JSON in Hibernate can have certain benefits in terms of performance and usability, but it also comes with its own set of considerations. 

It is now possible to specify json objects directly on JPA Entity Objects thanks to Vlad Mihalcea's great work with his "hypersistence-utils" (https://github.com/vladmihalcea/hypersistence-utils).
In this piece, he describes the entire process: https://vladmihalcea.com/how-to-map-json-objects-using-generic-hibernate-types

> As a general guideline, I would start with defining every `@OneToOne` relationship as Json.

## It is amazing, or not?

It certainly is. However, if you change the Json Model, the deserializer will fail to handle Object creation correctly. To ignore the changes, you can, of course, define a global catcher object, as seen below:

```java
@JsonIgnore
private Map<String, Object> additionalProperties = new LinkedHashMap<>();

@JsonAnyGetter
public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
}

@JsonAnySetter
public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
}
```

However, this causes the deserializer to catch just those properties that it was unable to address, which appears to be a poor answer to your problem.

# Solution

the above problem motivated me to write this little tool to change the json objects once needed. 
The integration is very simple and all you need is add the dependency and define a `changelog`

### Step 1

add this dependency to your project

```xml
    <dependency>
        <groupId>de.hoomit.projects</groupId>
        <artifactId>json-db-update</artifactId>
        <version>1.5</version>
    </dependency>
```

### Step 2

Imagine we have a mode like this:

![Customer_old.png](.github%2FCustomer_old.png)


All we want to do are the following changes:

1. remove `phone2`
2. rename `phone1` to `phone`
3. add a new attribute `email`

The new Person model looks like this:

![Customer_new.png](.github%2FCustomer_new.png)


create a file under `src/main/resources/config/jsondbupdate` (create `config/jsondbupdate` if necessary) and name it something like <timestamp>_update_customer.csv

**Note:** there is no naming convention. It is just enough to create a csv with a proper format

our file does look like this:

`20230825230322_update_customer.csv`

```csv
action;entity;field;attribute;newName
RENAME;Customer;person;phone1;phone
REMOVE;Customer;person;phone2;
ADD;Customer;person;email;
```

### Step 3

Attach the `json-db-update` to your current process. E.g. If you are using Spring Boot, you can attach it to Application start event:

```java
@Component
public class AppStartupRunner {
   
   @Autowired 
   private Environment env;

   @EventListener(ApplicationReadyEvent.class)
  final JsonDbUpdate jsonDbUpdate = new JsonDbUpdate();
        jsonDbUpdate.startup("de.hoomit.myapplication.domain",   //<---- this is the package where you keep your entities
                env.getProperty("spring.datasource.url"),
                env.getProperty("spring.datasource.username"),
                env.getProperty("spring.datasource.password"));
  }
}
```

## TL;LD Section :)

Let's explore how defining json objects approach can impact performance and usability:

1. Reduced Database Joins:
   In a traditional relational database setup, a OneToOne relationship usually involves creating separate tables for each entity and joining them using foreign keys. These joins can become performance bottlenecks, especially when retrieving data involving multiple OneToOne relationships. Storing these relationships as JSON within a single table can eliminate the need for these complex joins, leading to faster data retrieval.

2. Fewer Database Queries:
   By storing related data as JSON, you can often retrieve all the necessary information in a single database query, reducing the overall number of queries needed to assemble the complete entity. This can lead to more efficient data retrieval, particularly in scenarios where multiple related entities need to be fetched.

3. Simplified Data Structure:
   Storing related data as JSON can lead to a more straightforward and intuitive data structure. Instead of dealing with multiple table joins and complex SQL queries, developers can work with a single JSON object, which can improve code readability and maintainability.

4. Serialization and Deserialization:
   However, it's important to note that storing data as JSON requires serialization (converting data into a JSON format) and deserialization (converting JSON data back into objects) operations. These operations introduce some overhead, but modern frameworks and libraries have become quite efficient at handling these tasks.

5. Flexibility:
   Storing OneToOne relationships as JSON provides more flexibility in terms of schema changes. If you add new fields or properties to the related entity, you don't necessarily need to modify the database schema. This can simplify the development process, especially when dealing with evolving requirements.

Usability Considerations:

1. Readability and Debugging:
   While storing data as JSON can improve code readability and maintainability, it might make debugging and troubleshooting a bit more complex. It's easier to inspect data in a tabular format than in a JSON-encoded string. Additionally, tools designed for relational databases might not work as effectively with JSON data.

2. Querying Limitations:
   While retrieving a complete entity might be more efficient, querying specific data within the JSON-encoded fields can be trickier compared to using traditional SQL queries. Some databases offer JSON query capabilities, but they might not be as powerful or performant as native SQL queries.

3. Data Integrity:
   Storing data as JSON might affect data integrity enforcement. In a traditional relational setup, referential integrity (maintaining relationships between tables) is often enforced by the database engine itself. With JSON-encoded data, enforcing such constraints might require more manual effort.

4. Data Size and Indexing:
   Large JSON-encoded fields can lead to increased storage requirements. Additionally, indexing and searching within JSON fields might not be as efficient as indexing regular database columns.

## Conclusion:

Storing OneToOne relationships as JSON in Hibernate can indeed improve performance and usability in certain scenarios. It can reduce the need for complex joins and multiple queries, leading to faster data retrieval and a more intuitive data structure. However, this approach also brings challenges related to debugging, querying, data integrity, and potential storage overhead. Careful consideration of the specific use case and trade-offs is essential before deciding to use this approach.

