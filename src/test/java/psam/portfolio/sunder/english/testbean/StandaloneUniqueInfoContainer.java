package psam.portfolio.sunder.english.testbean;

import lombok.Builder;
import psam.portfolio.sunder.english.global.jpa.embeddable.Address;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class StandaloneUniqueInfoContainer implements UniqueInfoContainer {

    private int idIndex = 0;
    private int emailIndex = 0;
    private int phoneIndex = 0;
    private int academyNameIndex = 0;

    private final int size;

    private final List<String> uniqueIdList;
    private final List<String> uniqueEmailList;
    private final List<String> uniquePhoneNumberList;
    private final List<String> uniqueAcademyNameList;

    @Builder
    public StandaloneUniqueInfoContainer(int numVal, int loginIdLen, int emailLen, String emailDomain, int academyNameMinLen, int academyNameMaxLen) {
        size = numVal;
        uniqueIdList = generateUniqueIds(numVal, loginIdLen);
        uniqueEmailList = generateUniqueEmails(numVal, emailLen, emailDomain);
        uniquePhoneNumberList = generateUniquePhoneNumbers(numVal);
        uniqueAcademyNameList = generateUniqueAcademyNames(numVal, academyNameMinLen, academyNameMaxLen);
    }

    @Override
    public String getUniqueLoginId() {
        return uniqueIdList.get(idIndex++ % size);
    }

    @Override
    public String getUniqueEmail() {
        return uniqueEmailList.get(emailIndex++ % size);
    }

    @Override
    public String getUniquePhoneNumber() {
        return uniquePhoneNumberList.get(phoneIndex++ % size);
    }

    @Override
    public String getUniqueAcademyName() {
        return uniqueAcademyNameList.get(academyNameIndex++ % size);
    }

    private List<String> generateUniqueIds(int numberOfIds, int length) {
        List<String> uniqueIds = new ArrayList<>();
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

    private List<String> generateUniqueEmails(int numberOfEmails, int length, String domain) {
        List<String> uniqueEmails = new ArrayList<>();
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

    private List<String> generateUniquePhoneNumbers(int numberOfPhoneNumbers) {
        List<String> uniquePhoneNumbers = new ArrayList<>();
        while (uniquePhoneNumbers.size() < numberOfPhoneNumbers) {
            String phoneNumber = "010" + String.format("%08d", ThreadLocalRandom.current().nextInt(10000000, 100000000));
            uniquePhoneNumbers.add(phoneNumber);
        }
        return uniquePhoneNumbers;
    }

    private List<String> generateUniqueAcademyNames(int numberOfNames, int minLength, int maxLength) {
        List<String> uniqueNames = new ArrayList<>();
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

    @Override
    public Address getAnyAddress() {
        return Address.builder()
                .street("서울특별시 영등포구 의사당대로 1")
                .detail("국회")
                .postalCode("07233")
                .build();
    }
}
