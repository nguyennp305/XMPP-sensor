package com.xmpp.backend.utils;

import java.util.Iterator;
import java.util.List;

import org.igniterealtime.restclient.RestApiClient;
import org.igniterealtime.restclient.entity.UserEntities;
import org.igniterealtime.restclient.entity.UserEntity;
import org.igniterealtime.restclient.enums.SupportedMediaType;

import com.xmpp.backend.exception.SensorAlreadyExsistsException;
import com.xmpp.backend.exception.SensorNotFoundException;
import com.xmpp.backend.model.Sensor;
import com.xmpp.backend.model.SensorProperty;
import com.xmpp.backend.model.Sensors;
import com.xmpp.backend.xmpp.XmppConfig;

public class XmppApiPlugin {
    public RestApiClient restApiClient = new RestApiClient(StaticResource.DOMAIN, StaticResource.OPENFIREPORT,
            StaticResource.AUTHTOKEN);
    public RestApiClient restApiClientJson = new RestApiClient(StaticResource.DOMAIN, StaticResource.OPENFIREPORT,
            StaticResource.AUTHTOKEN, SupportedMediaType.JSON);

    public XmppApiPlugin() {
    }

    boolean isSensorExists(String id) {
        Sensors sensors = getAllSensors();
        List<Sensor> sensorsList = sensors.getSensors();
        boolean isExsist = false;
        for (Sensor sensor : sensorsList) {
            if (sensor.getId().equals(id)) {
                isExsist = true;
            }
        }
        return isExsist;
    }

    public Sensors getAllSensors() {
        UserEntities userEntities = restApiClient.getUsers();
        Sensors sensors = Transform.toSensors(userEntities);
        return sensors;
    }

    public Sensor getSensor(String id) {
        if (!isSensorExists(id)) {
            return null;
        }
        UserEntity userEntity = restApiClient.getUser(id);
        Sensor sensor = Transform.toSensor(userEntity);
        return sensor;
    }

    public Sensor createSensor(Sensor sensor) {
        String id = sensor.getId();
        if (isSensorExists(id)) {
            return null;
        }
        UserEntity userEntity = Transform.toCreateUser(sensor);
        restApiClientJson.createUser(userEntity);
        try {
            XmppConfig xmppSensor = new XmppConfig(userEntity.getUsername(), userEntity.getPassword());
            xmppSensor.connect();
            GenerateSensorData.xmppSensors.add(xmppSensor);
            xmppSensor.sensorListenMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sensor;
    }

    public void deleteSensor(String id) {
        // if (!isSensorExists(id)) {
        // throw new SensorNotFoundException("Sensor not found");
        // }
        // restApiClient.deleteSessions(id);
        Iterator<XmppConfig> i = GenerateSensorData.xmppSensors.iterator();
        while (i.hasNext()) {
            XmppConfig xmppSensor = i.next();
            if (xmppSensor.getUserName().equals(id)) {
                xmppSensor.disconnect();
                i.remove();
            }
        }
        restApiClient.deleteUser(id);
    }

    public Sensor updateSensor(String id, Sensor newSensor) {
        UserEntity oldUser = restApiClient.getUser(id);
        Sensor oldSensor = getSensor(id);
        if (oldSensor == null) {
            throw new SensorNotFoundException("Sensor not found");
        }
        UserEntity newUser = Transform.toUpdateUser(newSensor, oldUser);
        restApiClientJson.updateUser(newUser);
        return newSensor;
    }

    public List<SensorProperty> getSensorProperties(String id) {
        Sensor sensor = getSensor(id);
        if (sensor == null) {
            throw new SensorNotFoundException("Sensor not found");
        }
        return sensor.getProps();
    }

    public String getMemory(Sensor sensor) {
        for (SensorProperty prop : sensor.getProps()) {
            if (prop.getKey().equals("mem")) {
                return prop.getValue();
            }
        }
        return "not found";
    }
}
