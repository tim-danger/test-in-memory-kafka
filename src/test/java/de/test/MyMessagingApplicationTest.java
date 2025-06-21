package de.test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import io.smallrye.reactive.messaging.memory.InMemorySource;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

@QuarkusTest
@QuarkusTestResource(KafkaTestResourceLifecycleManager.class)
public class MyMessagingApplicationTest {

    @Inject
    @Any
    InMemoryConnector inMemoryConnector;

    @Inject
    PersonRepository repository;

    private final static String[] HEADERS = { "Employee ID", "Full Name", "Job Title",
            "Department", "Business Unit", "Gender", "Ethnicity", "Age",
            "Hire Date", "Annual Salary", "Bonus %", "Country", "City", "Exit Date" };

    @Test
    public void testSendingMessage() throws IOException, ParseException, InterruptedException {
        Reader in = new FileReader("src/test/resources/Employee Sample Data 4.csv");

        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader(HEADERS)
                .setSkipHeaderRecord(true)
                .get();
        Iterable<CSVRecord> records = csvFormat.parse(in);

        int sentMessages = 0;
        long startTime = System.currentTimeMillis();
        while (records.iterator().hasNext()) {
            EmployeeDto dto = employeeDtoFromRecord(records.iterator().next());
            sendMyObjectToSink(dto);
            sentMessages++;
        }
        long endTime = System.currentTimeMillis();

        int finalSentMessages = sentMessages;
        AtomicLong receivingEnd = new AtomicLong();
        AtomicLong messagesInDb = new AtomicLong(this.repository.numberOfAllEmployees());
        Assertions.assertTimeoutPreemptively(Duration.ofMinutes(1), () -> {
            while (messagesInDb.get() < finalSentMessages) {
                // do nothing
                Thread.sleep(100L);
                messagesInDb.set(this.repository.numberOfAllEmployees());
            }
            receivingEnd.set(System.currentTimeMillis());
        });

        Duration sendingDuration = Duration.ofMillis(endTime - startTime);
        Duration receivingDuration = Duration.ofMillis(receivingEnd.get() - endTime);

        System.out.println("Messages Sent: " + sentMessages + ", \n\rMessages Received: " + messagesInDb.get() +
                ", \n\rDuration for sending: " + formatDuration(sendingDuration) + ", \n\rDuration for receiving: " + formatDuration(receivingDuration));
        Assertions.assertEquals(sentMessages, (int) messagesInDb.get(), "Die gesendeten Nachrichten (" + sentMessages + ") stimmen nicht mit den empfangenen (" + messagesInDb.get() + ") Nachrichten überein!");
    }

    private String formatDuration(Duration duration) {
        return String.format("%02d:%02d:%02d", duration.toHoursPart(), duration.toMinutesPart(),
                duration.toSecondsPart());
    }

    private EmployeeDto employeeDtoFromRecord(CSVRecord record) throws ParseException {
        EmployeeDto dto = new EmployeeDto();
        dto.setEmployeeId(record.get(HEADERS[0]));
        dto.setName(record.get(HEADERS[1]));
        dto.setJobTitle(record.get(HEADERS[2]));
        dto.setDepartment(record.get(HEADERS[3]));
        dto.setBusinessUnit(record.get(HEADERS[4]));
        dto.setGender(record.get(HEADERS[5]));
        dto.setEthnicity(record.get(HEADERS[6]));
        dto.setAge(parseAge(record.get(HEADERS[7])));
        dto.setHireDate(convertLocalDateFromString(record.get(HEADERS[8])));
        dto.setSalary(parse(record.get(HEADERS[9]), Locale.US).doubleValue());
        dto.setBonus(parse(record.get(HEADERS[10]), Locale.US).doubleValue());
        dto.setCountry(record.get(HEADERS[11]));
        dto.setCity(record.get(HEADERS[12]));
        dto.setExitDate(convertLocalDateFromString(record.get(HEADERS[13])));
        return dto;
    }

    private int parseAge(String ageAsString) {
        if (ageAsString == null || ageAsString.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(ageAsString);
    }

    private BigDecimal parse(final String amount, final Locale locale) throws ParseException {
        if (amount == null || amount.isEmpty()) {
            return BigDecimal.ZERO;
        }
        final NumberFormat format = NumberFormat.getNumberInstance(locale);
        if (format instanceof DecimalFormat) {
            ((DecimalFormat) format).setParseBigDecimal(true);
        }
        return (BigDecimal) format.parse(amount.replaceAll("[^\\d.,%]",""));
    }

    private LocalDate convertLocalDateFromString(String localDateAsString) {
        if (localDateAsString == null || localDateAsString.isEmpty()) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
        formatter = formatter.withLocale(Locale.US);  // Locale specifies human language for translating, and cultural norms for lowercase/uppercase and abbreviations and such. Example: Locale.US or Locale.CANADA_FRENCH
        return LocalDate.parse(localDateAsString, formatter);
    }


    private void sendMyObjectToSink(EmployeeDto input) {
        InMemorySource<EmployeeDto> eventQueue = inMemoryConnector.source("test");
        eventQueue.send(input);
    }

}
