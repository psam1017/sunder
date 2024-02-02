package psam.portfolio.sunder.english.testbean;

import psam.portfolio.sunder.english.global.jpa.embeddable.Address;

public interface UniqueInfoContainer {

    String getUniqueId();
    String getUniqueEmail();
    String getUniquePhoneNumber();
    String getUniqueAcademyName();
    Address getAnyAddress();
}
