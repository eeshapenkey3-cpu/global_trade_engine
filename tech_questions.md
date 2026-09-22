#### **what does dashboard controller even do and why do i even need it**
#### **why do i need to run dashboard controller everytime for the website to run?**
#### **how do i deploy the website?**
#### **should i sync my excel files to the java files and why?**
#### **how do i connect gemini api into my website?**
#### **the global currency dataset doesnt have actual realsitic conversions, do i make one or find another one**
#### **the currency conversion rates dataset wasn't working because it wasn't actual useful data, what can i do instead?**
#### **why do i need repository for the java files?**
* Repository is the translator, like doing both SQL and java work, fetching rows and turning them into java objects etc.
* controller translates web page requests, service is the java logic and repos handle translation
* othewise controller would have to read web requests, make sql syntax, and translate
#### **why do i need more than one repository? can't i just have 1?**
#### **why does repository have to make sql syntax? what's the point of sql in this?**
#### **how can the user safely download the engine and run it on their desktop?**
#### **why does the only thing the repository have is e.g Optional<ShippingRoute> findByRouteId(String routeId);**
* it's java way of sql, so findByRouteId(String routeId) is SELECT * FROM shipping_route WHERE route_id LIKE 'xyz'
#### **why can't intellij resolve 'Optional', 'isEmpty()', 'get()'?**
import java.util.Optional needs to be clearly mentioned on the top
#### **where does the literal shippingRoute java actually live?**
#### **what is a java interface and method signature?**
#### **what is Spring data JPA?**
#### **what is api?**
#### **what is interface?**
