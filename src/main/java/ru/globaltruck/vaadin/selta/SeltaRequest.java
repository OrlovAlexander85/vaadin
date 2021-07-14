package ru.globaltruck.vaadin.selta;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class SeltaRequest {
    private String id;
    private CompanyDescription companyDescription;
    private String offerNumber;
    private String orderNumber;
    private String offerType;
    private LocalDate startDate;
    private LocalTime startTime;
    private Customer customer;
    private Customer shipper;
    private List<Route> routes;
//    private List<Cargo> cargo;
//    <cargo>
//        <name>Непрофиль</name>
//        <pallet>33</pallet>
//        <temperatureConditions>
//            <min>0</min>
//            <max>0</max>
//            <recommended/>
//            <mode/>
//        </temperatureConditions>
//        <unitOfMeasure>кг .</unitOfMeasure>
//        <loadingType>
//            <name>Задняя</name>
//        </loadingType>
//        <weight>13730</weight>
//    </cargo>
//    <rateInfo>
//        <currency>
//    <name> Российский рубль</name>
//            <shortName>руб .</shortName>
//        </currency>
//        <rate>28400</rate>
//        <taxRate>20</taxRate>
//        <AdditionalServices>0</AdditionalServices>
//    </rateInfo>
//    <carRequirements>
//        <mountingTools>true</mountingTools>
//        <truckType>
//            <name>Рефрижератор</name>
//        </truckType>
//        <requirementForEquipment>
//            <crossbarRequired value="false"/>
//            <beltsRequired value="false"/>
//            <fasteningDevicesRequired value="false"/>
//            <otherRequired value="false">
//                <text/>
//            </otherRequired>
//        </requirementForEquipment>
//    </carRequirements>
//    <specialConditions>
}
