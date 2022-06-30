<div align="center">
<br/>
  <p>
    <img src="https://img.shields.io/static/v1?label=Version&message=Alpha-1.0.0&color=12c970&logoColor=white" alt="Version" />
	<br>
	</p>
  </p>
</div>

# SquareTree
SquareTree is a Java library to create packet traffic of customized nodes and modules at server-side.
The application consists of several parts.
* SquareTree-Connector (Client connect to the server)
* SqareTree-Network (network library)
* SqareTree-Server (Server application)

The Server-Application has a controlling terminal and a module system.

| OS      	| Supported 	|
|---------	|-----------	|
| Windows 	| yes       	|
| Linux   	| yes       	|
| Macos   	| no        	|

## Documentation
* [Wiki](https://github.com/APICodeYT/SquareTree/wiki)
* [Docs]

## Maven

```xml
<dependencies>
  <!-- Server API -->
  <dependency>
    <groupId>net.apicode</groupId>
    <artifactId>squaretree-server</artifactId>
    <version>{{VERSION}}</version>
  </dependency>
  
  <!-- Connector API -->
  <dependency>
    <groupId>net.apicode</groupId>
    <artifactId>squaretree-connector</artifactId>
    <version>{{VERSION}}</version>
  </dependency>
</dependencies>
```
