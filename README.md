# Dropwizard form based authentication example
This example project is a reminder to myself of how to work around a specific use case when building simple front end applications with a backend API.

You want to build a simple front end in dropwizard alonside an API and restrict access to the application.

You want a login page, but dropwizard doesn't support form based authentication.

## How to use

Configure dropwizard to use a different server and specify the URIs for the login, error and logout pages (this requires the new server factory to be registered in META-INF/services, see How it works for details)

    server:
      type: form-based-authentication
      loginUri: /login.html
      loginErrorUri: /loginerror.html
      logoutUri: /logout
      applicationContextPath: /
      rootPath: /api
      applicationConnectors:
        - type: http
          port: 8080

In your dropwizard application's run method, use the utility class FormBasedEnvironmentBinder to set everything up:

    FormBasedEnvironmentBinder<Config> formBasedBinder = new FormBasedEnvironmentBinder<>();
    formBasedBinder
        .using(config)
        .protectAssetsAt("/")
        .allow("/css")
        .allow("/favicon.ico")
        .registerLoginService(
            new SimpleLoginService()
                .addUser("user", "letmein", "admin")
            )
        .bindTo(environment);

###### protectAssetsAt(String path)
This will add a new servlet filter to jetty that will redirect requests to the configured login page if a valid session does not exist.

If you register an AssetBundle in your initialize method, this filter can restrict access for the configured path.

###### allow(String path)
This will whitelist certain paths from the above filter to allow you to serve up css, javascript, favicons or any other resources you need for your login page.

###### registerLoginService(LoginService service)
You need to add your own (or a built in) login service to authenticate users and expose role membership.  This sample project includes a SimpleLoginService that lets you quickly add users and roles.

 



#### How it works
When you start a dropwizard application, it reads the Configuration yml file and uses jackson to instantiate / map the values in this file to the appropriate configuration objects.

Baked into the dropwizard Configuration superclass, is the registration of a server factory:

    @Valid
    @NotNull
    private ServerFactory server = new DefaultServerFactory();
 

One of the many jobs the ServerFactory class performs is to create the appropriate Servlets for your dropwizard application and register these with jetty.

It is here that you can extend and modify how these servlets are configured.

Jackson instantiates the appropriate server factory from the type specified in the config:

ServerFactory
   
    @JsonTypeInfo(
        use = Id.NAME,
        property = "type",
        defaultImpl = DefaultServerFactory.class 
    )
    public interface ServerFactory extends Discoverable {

config.yml

    server:
      type: simple 
 

SimpleServerFactory

    @JsonTypeName("simple")
    public class SimpleServerFactory extends AbstractServerFactory {


You can see above that when you have type: simple, the annotation on SimpleServerFactory instructs jackson to instantiate this class


##### Register a new server factory
As far as I can tell, this is not very well documented. 

You need to introduce alternative server factories by creating a new file on your class path (resources) here:

    META-INF/services/io.dropwizard.server.ServerFactory

    NOTE: this is the file name (not a package/path) io.dropwizard.server.ServerFactory

In this file place the fully qualified name of your alternative factory/factories. In our case, this is:  

    uk.co.chunkybacon.dwformbased.auth.FormBasedAuthenticationServerFactory
    
Since this class defines this JsonTypeName:

    @JsonTypeName("form-based-authentication")
    public class FormBasedAuthenticationServerFactory extends DefaultServerFactory {
    
All you need to do is specify this in the config for your application:

    server:
      type: form-based-authentication

    
##### Use the new ServerFactory to setup Form Authentication
Before the ServerFactory creates the servlet for the dropwizard application, the FormBasedAuthenticationServerFactory adds in jetty's form authentication:  

    FormAuthenticator authenticator = new FormAuthenticator(
        this.loginUri,
        this.loginErrorUri,
        false
    );

    ConstraintSecurityHandler handler = new ConstraintSecurityHandler();
    handler.setAuthenticator(authenticator);
    
This wires in the code that handles j_security_check that you can POST j_username and j_password to authenticate users and create a session for them.

##### Register RolesAllowedDynamicFeature
This allows you to annotate your dropwizard controllers with @RolesAllowed, specifying the role required to access.

Since dropwizard reads the roles from the jetty session, once you are logged in and have a session, you have access to the API and any protected assets. 

##### Register a servlet filter to handle logout
FormBasedLogoutFilter is a simple filter that will invalidate your session if you hit the configured uri.

##### Register a servlet filter to restrict access to assets
This sample project includes the class ProtectedUriFilter.  This can be configured to redirect you to the login page if you do not have a session and you attempt to access the dropwizard API or any protected assets.

##### Register an exception mapper to redirect access attempts at the dropwizard app to the login page
If required, you can redirect unauthenticateed access to the API to the login page.  It's not necessary, but just shows how you can do this if you want.
  
