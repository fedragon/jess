* Add more predefined functions:
 - "1" isNot 123
 - Improve JsArrayRule tests to handle unsupported input types

* Figure out the best return type for rules' apply():
 - Plain old boolean --> not really helpful to understand what went wrong
 - Either[true, "Something went wrong"] --> hardcoding strings
 - Either[Geldig, Ongeldig(JsValue)] --> to be explored

Also, apply should never throw exceptions in order to allow validation to continue