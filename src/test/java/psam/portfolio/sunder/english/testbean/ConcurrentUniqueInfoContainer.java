package psam.portfolio.sunder.english.testbean;

import lombok.Builder;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("unused")
public class ConcurrentUniqueInfoContainer implements UniqueInfoContainer {

    private final Iterator<String> uniqueIdIterator;
    private final Iterator<String> uniqueEmailIterator;
    private final Iterator<String> uniquePhoneNumberIterator;
    private final Iterator<String> uniqueAcademyNameIterator;
    private final Iterator<String> uniqueAttendanceIdIterator;

    @Builder
    public ConcurrentUniqueInfoContainer(int numberOfCollection, int userNameLen, int userEmailLen, String emailDom, int academyNameMinLen, int academyNameMaxLen, int attendateIdLen) {
        uniqueIdIterator = generateUniqueIds(numberOfCollection, userNameLen).iterator();
        uniqueEmailIterator = generateUniqueEmails(numberOfCollection, userEmailLen, emailDom).iterator();
        uniquePhoneNumberIterator = generateUniquePhoneNumbers(numberOfCollection).iterator();
        uniqueAcademyNameIterator = generateUniqueAcademyNames(numberOfCollection, academyNameMinLen, academyNameMaxLen).iterator();
        uniqueAttendanceIdIterator = generateUniqueAttendanceIds(numberOfCollection, attendateIdLen).iterator();
    }

    @Override
    public String getUniqueLoginId() {
        String id = uniqueIdIterator.next();
        uniqueIdIterator.remove();
        return id;
    }

    @Override
    public String getUniqueEmail() {
        String email = uniqueEmailIterator.next();
        uniqueEmailIterator.remove();
        return email;
    }

    @Override
    public String getUniquePhoneNumber() {
        String phoneNumber = uniquePhoneNumberIterator.next();
        uniquePhoneNumberIterator.remove();
        return phoneNumber;
    }

    @Override
    public String getUniqueAcademyName() {
        String academyName = uniqueAcademyNameIterator.next();
        uniqueAcademyNameIterator.remove();
        return academyName;
    }

    @Override
    public String getUniqueAttendanceId() {
        String attendanceId = uniqueAttendanceIdIterator.next();
        uniqueAttendanceIdIterator.remove();
        return attendanceId;
    }

    private Set<String> generateUniqueIds(int numberOfIds, int length) {
        Set<String> uniqueIds = Collections.synchronizedSet(new HashSet<>());
        while (uniqueIds.size() < numberOfIds) {
            String uuid = UUID.randomUUID().toString();
            uuid = uuid.replaceAll("-", "").toLowerCase();
            if (uuid.length() > length) {
                uuid = "id" + uuid.substring(0, length);
            }
            uniqueIds.add(uuid);
        }
        return uniqueIds;
    }

    private Set<String> generateUniqueEmails(int numberOfEmails, int length, String domain) {
        Set<String> uniqueEmails = Collections.synchronizedSet(new HashSet<>());
        while (uniqueEmails.size() < numberOfEmails) {
            String uuid = UUID.randomUUID().toString();
            uuid = uuid.replaceAll("-", "").toLowerCase();
            if (uuid.length() > length) {
                uuid = "email" + uuid.substring(0, length);
            }
            uniqueEmails.add(uuid + "@" + domain);
        }
        return uniqueEmails;
    }

    private Set<String> generateUniquePhoneNumbers(int numberOfPhoneNumbers) {
        Set<String> uniquePhoneNumbers = Collections.synchronizedSet(new HashSet<>());
        while (uniquePhoneNumbers.size() < numberOfPhoneNumbers) {
            String phoneNumber = "010" + String.format("%08d", ThreadLocalRandom.current().nextInt(10000000, 100000000));
            uniquePhoneNumbers.add(phoneNumber);
        }
        return uniquePhoneNumbers;
    }

    private Set<String> generateUniqueAcademyNames(int numberOfNames, int minLength, int maxLength) {
        Set<String> uniqueNames = Collections.synchronizedSet(new HashSet<>());
        while (uniqueNames.size() < numberOfNames) {
            StringBuilder name = new StringBuilder();
            int length = ThreadLocalRandom.current().nextInt(minLength, maxLength + 1);
            for (int i = 0; i < length - 2; i++) {
                char ch = (char) ThreadLocalRandom.current().nextInt(0xAC00, 0xD7A4);
                name.append(ch);
            }
            name.append("학원");
            uniqueNames.add(name.toString());
        }
        return uniqueNames;
    }

    private Set<String> generateUniqueAttendanceIds(int numberOfIds, int length) {
        Set<String> uniqueIds = Collections.synchronizedSet(new HashSet<>());
        while (uniqueIds.size() < numberOfIds) {
            String uuid = UUID.randomUUID().toString();
            if (uuid.length() > length) {
                uuid = "attend" + uuid.substring(0, length);
            }
            uniqueIds.add(uuid);
        }
        return uniqueIds;
    }
}
