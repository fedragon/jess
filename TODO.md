* Add more predefined functions:
 - "1" isNot 123
 - "1" in (1, 2)
 - "1" notIn (4, 5)
 - "2" in ("a", "b")
 - "2" in r"<regex>"

* Figure out the best return type for rules' apply():
 - Plain old boolean --> not really helpful to understand what went wrong
 - Either[true, "Something went wrong"] --> hardcoding strings
 - Either[Geldig, Ongeldig(JsValue)] --> to be explored

Also, apply should never throw exceptions in order to allow validation to continue