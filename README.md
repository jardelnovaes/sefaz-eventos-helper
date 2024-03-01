# Simple Kotlin Swing App for Send Sefaz Events
Sefaz is a Brazilian Government department that every Brazilian State has, one of it responsibility is to approve invoice emissions.  
Invoices in Brazil is quite complex and can have many events for each invoice.  
This App helps to generate and send events of "Manifestação".  
In the future it can help with other events, like cancelling an invoice.

### Configuration
You'll need to create a file called `application.properties` at the root directory of the app.  
Please, see the `application-example.properties`.  

### Packaging 
Just run:
```bash
$ mvn package
```

It'll produce `target/sefaz-eventos-helper.tar.gz`, to install it on a new computer you'll need to extract it.

### Running
The default runner script has a *fixed* JAVA_HOME path, please change it if needed.  
It needs `Java 17+`  

Use:
```bash
$ ./sefaz-eventos-helper.sh
```
or
```bat
C:\> sefaz-eventos-helper.bat
```

#### Running source code using maven 
You can do it using:
```bash
$ mvn exec:java -Dexec.mainClass=br.com.jardelnovaes.sefaz.eventos.helper.Launcher
```
