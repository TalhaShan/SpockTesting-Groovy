package com.example.demo.spock

import spock.lang.Specification

class Example extends Specification {
    def "should be a simple assertion"() {
        expect:
        1 == 1
    }

    def "2+2 should be four"() {
        given:
        int left = 2
        int right = 2
        when:
        int res = left + right
        then:
        res == 4
    }

    def "should be able to remove from list"() {
        given:
        def list = [12, 2, 3]
        when:
        list.remove(0)
        then:
        list == [2, 1, 3]
    }

    def "verify multiple properties of a ShoppingCart"() {
        given:
        ShoppingCart cart = new ShoppingCart()
        cart.addItem("Apple", 3)
        cart.addItem("Banana", 2)

        expect:
        with(cart) {
            totalItems == 5
            totalPrice == 10.00
            items.contains("Apple")
            items.contains("Banana")
        }
    }

    def "Should get an index out of bounds when removing a non-existent item"() {
        given:
        def list = [1, 2, 3, 4]

        when:
        list.remove(20)

        then:
        thrown(IndexOutOfBoundsException)
        list.size() == 4
    }

    def "Method should return power of given number"(int a, int b, int c) {
        expect:
        Math.pow(a, b) == c

        where:
        a | b | c
        1 | 2 | 1
        2 | 2 | 4
        3 | 2 | 9
    }

    def "Calculate"() {
        given:
        paymentGateway.makePayment(20) >> true

        when:
        def result = paymentGateway.makePayment(20)

        then:
        result == true
    }
    /*
    What’s interesting here, is how Spock makes use of Groovy’s operator overloading in order to stub method calls. With Java, we have to call real methods, which arguably means that the resulting code is more verbose and potentially less expressive.
     */

    def "With different param"() {
        paymentGateway.makePayment(_) >> true
        paymentGateway.makePayment(_) >>> [true, true, false, true]
    }
   /*Verify */
    def "Should verify notify was called"() {
        given:
        def notifier = Mock(Notifier)

        when:
        notifier.notify('foo')

        then:
        1 * notifier.notify('foo')


    }

//    Spock is leveraging Groovy operator overloading again. By multiplying our mocks method call by one, we are saying how many times we expect it to have been called.
//
//    If our method had not been called at all or alternatively had not been called as many times as we specified, then our test would have failed to give us an informative Spock error message. Let’s prove this by expecting it to have been called twice:

//    2 * notifier.notify('foo')

//    Too few invocations for:
//
//    2 * notifier.notify('foo')   (1 invocation)

//    Just like stubbing, we can also perform looser verification matching. If we didn’t care what our method parameter was, we could use an underscore:
//
//    2 * notifier.notify(_)
//    Copy
//            Or if we wanted to make sure it wasn’t called with a particular argument, we could use the not operator:
//
//    2 * notifier.notify(!'foo')

    /*
    Spock 2.0 also introduced the possibility of replacing all instances of a class with a Mock globally, allowing us to mock objects that we are not directly instantiating in the Tests. It simplifies testing by removing the need for injecting the Mocks explicitly into the code we are testing:

public class UtilityClass {
    public static String getMessage() {
        return "Original Message";
    }
}

public class MessageService {
    public String fetchMessage() {
        return UtilityClass.getMessage();
    }
}
Copy
In the above code, we have a class using another, writing tests for MessageService with a UtilityClass mock is difficult because it is not designed to allow us to inject a mock for it.

This is when Global Mocks come more than handy:
     */

    class MessageServiceTest extends Specification {
        def "should use global mock for UtilityClass"() {
            given:
            def utilityMock = GroovySpy(UtilityClass, global: true)
            utilityMock.getMessage() >> "Mocked Message"

            when:
            MessageService service = new MessageService()
            String message = service.fetchMessage()

            then:
            1 * utilityMock.getMessage()
            message == "Mocked Message"
        }
        /*
            In the above example, we create a Global Mock using the GroovySpy class, note that we do not mock MessageService or inject the mock UtilityClass in any way, nevertheless, we can successfully assert expectations on the mock, or that we obtained the value we stubbed instead of the original one.
         */
    }



}
