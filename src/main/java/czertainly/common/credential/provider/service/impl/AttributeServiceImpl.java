package czertainly.common.credential.provider.service.impl;

import com.czertainly.api.exception.ValidationError;
import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.interfaces.connector.AttributesController;
import com.czertainly.api.model.client.attribute.RequestAttributeDto;
import com.czertainly.api.model.common.attribute.AttributeProperties;
import com.czertainly.api.model.common.attribute.AttributeType;
import com.czertainly.api.model.common.attribute.BaseAttribute;
import com.czertainly.api.model.common.attribute.DataAttribute;
import com.czertainly.api.model.common.attribute.content.AttributeContentType;
import com.czertainly.api.model.common.attribute.content.BaseAttributeContent;
import com.czertainly.api.model.common.attribute.content.FileAttributeContent;
import com.czertainly.api.model.common.attribute.content.SecretAttributeContent;
import com.czertainly.api.model.common.attribute.content.StringAttributeContent;
import com.czertainly.core.util.AttributeDefinitionUtils;
import czertainly.common.credential.provider.service.AttributeService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class AttributeServiceImpl implements AttributeService {
    private static final Logger logger = LoggerFactory.getLogger(AttributesController.class);

    // Soft key store attribute names
    public static final String ATTRIBUTE_KEYSTORE_TYPE = "keyStoreType";
    public static final String ATTRIBUTE_KEYSTORE = "keyStore";
    public static final String ATTRIBUTE_KEYSTORE_PASSWORD = "keyStorePassword";
    public static final String ATTRIBUTE_TRUSTSTORE_TYPE = "trustStoreType";
    public static final String ATTRIBUTE_TRUSTSTORE = "trustStore";
    public static final String ATTRIBUTE_TRUSTSTORE_PASSWORD = "trustStorePassword";

    // Soft key store attribute labels
    public static final String ATTRIBUTE_KEYSTORE_TYPE_LABEL = "Key Store Type";
    public static final String ATTRIBUTE_KEYSTORE_LABEL = "Key Store";
    public static final String ATTRIBUTE_KEYSTORE_PASSWORD_LABEL = "Key Store Password";
    public static final String ATTRIBUTE_TRUSTSTORE_TYPE_LABEL = "Trust Store Type";
    public static final String ATTRIBUTE_TRUSTSTORE_LABEL = "Trust Store";
    public static final String ATTRIBUTE_TRUSTSTORE_PASSWORD_LABEL = "Trust Store Password";

    // Basic auth attribute names
    public static final String ATTRIBUTE_USERNAME = "username";
    public static final String ATTRIBUTE_PASSWORD = "password";

    // Basic auth attribute names
    public static final String ATTRIBUTE_USERNAME_LABEL = "Username";
    public static final String ATTRIBUTE_PASSWORD_LABEL = "Password";

    // API Key attribute names
    public static final String ATTRIBUTE_APIKEY = "apiKey";
    public static final String ATTRIBUTE_APIKEY_LABEL = "API Key";

    @Value("${keyStore.supportedTypes}")
    private ArrayList<String> supportedKeyStoreTypes;

    @Override
    public List<BaseAttribute> getAttributes(String kind) {
        // TODO: kinds should be defined as constants, all Pascal Case
        switch (kind) {
            case "SoftKeyStore":
                return getSofKeyStoreAttributes();
            case "Basic":
                return getBasicAttributes();
            case "ApiKey":
                return getApiKeyAttributes();
            default:
                throw new ValidationException(ValidationError.create("Unsupported credential kind {}", kind));
        }
    }

    @Override
    public boolean validateAttributes(String kind, List<RequestAttributeDto> attributes) {
        switch (kind) {
            // TODO: kinds should be defined as constants, all Pascal Case
            case "SoftKeyStore":
                return validateSofKeyStoreAttributes(attributes);
            case "Basic":
                return validateBasicAttributes(attributes);
            case "ApiKey":
                return validateApiKeyAttributes(attributes);
            default:
                throw new ValidationException(ValidationError.create("Unsupported credential kind {}", kind));
        }
    }

    private List<BaseAttribute> getSofKeyStoreAttributes() {
        List<BaseAttribute> attrs = new ArrayList<>();

        List<BaseAttributeContent> keyStoreTypes = new ArrayList<>();
        for (String supportedKeyStoreType : supportedKeyStoreTypes) {
            StringAttributeContent keyStoreType = new StringAttributeContent();
            keyStoreType.setReference(supportedKeyStoreType);
            keyStoreType.setData(supportedKeyStoreType);
            keyStoreTypes.add(keyStoreType);
        }

        DataAttribute keyStoreType = new DataAttribute();
        keyStoreType.setUuid("e334e055-900e-43f1-aedc-54e837028de0");
        keyStoreType.setName(ATTRIBUTE_KEYSTORE_TYPE);
        AttributeProperties keyStoreTypeProperties = new AttributeProperties();
        keyStoreType.setDescription("Key store type");
        keyStoreType.setType(AttributeType.DATA);
        keyStoreType.setContentType(AttributeContentType.STRING);
        keyStoreTypeProperties.setLabel(ATTRIBUTE_KEYSTORE_TYPE_LABEL);
        keyStoreTypeProperties.setRequired(true);
        keyStoreTypeProperties.setReadOnly(false);
        keyStoreTypeProperties.setVisible(true);
        keyStoreTypeProperties.setList(true);
        keyStoreTypeProperties.setMulti(false);
        keyStoreType.setContent(keyStoreTypes);
        keyStoreTypeProperties.setGroup("Key Store");
        keyStoreType.setProperties(keyStoreTypeProperties);
        attrs.add(keyStoreType);

        DataAttribute keyStore = new DataAttribute();
        keyStore.setUuid("6df7ace9-c501-4d58-953c-f8d53d4fb378");
        keyStore.setName(ATTRIBUTE_KEYSTORE);
        keyStore.setDescription("Key store file");
        keyStore.setType(AttributeType.DATA);
        keyStore.setContentType(AttributeContentType.FILE);
        AttributeProperties keyStoreProperties = new AttributeProperties();
        keyStoreProperties.setLabel(ATTRIBUTE_KEYSTORE_LABEL);
        keyStoreProperties.setRequired(true);
        keyStoreProperties.setReadOnly(false);
        keyStoreProperties.setVisible(true);
        keyStoreProperties.setList(false);
        keyStoreProperties.setMulti(false);
        keyStoreProperties.setGroup("Key Store");
        keyStore.setProperties(keyStoreProperties);
        attrs.add(keyStore);

        DataAttribute keyStorePassword = new DataAttribute();
        keyStorePassword.setUuid("d975fe42-9d09-4740-a362-fc26f98e55ea");
        keyStorePassword.setName(ATTRIBUTE_KEYSTORE_PASSWORD);
        keyStorePassword.setDescription("Key store password");
        keyStorePassword.setType(AttributeType.DATA);
        keyStorePassword.setContentType(AttributeContentType.SECRET);
        AttributeProperties keyStorePasswordProperties = new AttributeProperties();
        keyStorePasswordProperties.setLabel(ATTRIBUTE_KEYSTORE_PASSWORD_LABEL);
        keyStorePasswordProperties.setRequired(true);
        keyStorePasswordProperties.setReadOnly(false);
        keyStorePasswordProperties.setVisible(true);
        keyStorePasswordProperties.setList(false);
        keyStorePasswordProperties.setMulti(false);
        keyStorePasswordProperties.setGroup("Key Store");
        keyStorePassword.setProperties(keyStorePasswordProperties);
        attrs.add(keyStorePassword);

        DataAttribute trustStoreType = new DataAttribute();
        trustStoreType.setUuid("c4454807-805a-44e2-81d1-94b56e993786");
        trustStoreType.setName(ATTRIBUTE_TRUSTSTORE_TYPE);
        trustStoreType.setDescription("Trust store type");
        trustStoreType.setType(AttributeType.DATA);
        trustStoreType.setContentType(AttributeContentType.STRING);
        AttributeProperties trustStoreTypeProperties = new AttributeProperties();
        trustStoreTypeProperties.setLabel(ATTRIBUTE_TRUSTSTORE_TYPE_LABEL);
        trustStoreTypeProperties.setRequired(false);
        trustStoreTypeProperties.setReadOnly(false);
        trustStoreTypeProperties.setVisible(true);
        trustStoreTypeProperties.setList(true);
        trustStoreTypeProperties.setMulti(false);
        trustStoreType.setContent(keyStoreTypes);
        trustStoreTypeProperties.setGroup("Trust Store");
        trustStoreType.setProperties(trustStoreTypeProperties);
        attrs.add(trustStoreType);

        DataAttribute trustStore = new DataAttribute();
        trustStore.setUuid("6a245220-eaf4-44cb-9079-2228ad9264f5");
        trustStore.setName(ATTRIBUTE_TRUSTSTORE);
        trustStore.setDescription("Trust store file");
        trustStore.setType(AttributeType.DATA);
        trustStore.setContentType(AttributeContentType.FILE);
        AttributeProperties trustStoreProperties = new AttributeProperties();
        trustStoreProperties.setLabel(ATTRIBUTE_TRUSTSTORE_LABEL);
        trustStoreProperties.setRequired(false);
        trustStoreProperties.setReadOnly(false);
        trustStoreProperties.setVisible(true);
        trustStoreProperties.setList(false);
        trustStoreProperties.setMulti(false);
        trustStoreProperties.setGroup("Trust Store");
        trustStore.setProperties(trustStoreProperties);
        attrs.add(trustStore);

        DataAttribute trustStorePassword = new DataAttribute();
        trustStorePassword.setUuid("85a874da-1413-4770-9830-4188a37c95ee");
        trustStorePassword.setName(ATTRIBUTE_TRUSTSTORE_PASSWORD);
        trustStorePassword.setDescription("Trust store password");
        trustStorePassword.setType(AttributeType.DATA);
        trustStorePassword.setContentType(AttributeContentType.SECRET);
        AttributeProperties trustStorePasswordProperties = new AttributeProperties();
        trustStorePasswordProperties.setLabel(ATTRIBUTE_TRUSTSTORE_PASSWORD_LABEL);
        trustStorePasswordProperties.setRequired(false);
        trustStorePasswordProperties.setReadOnly(false);
        trustStorePasswordProperties.setVisible(true);
        trustStorePasswordProperties.setList(false);
        trustStorePasswordProperties.setMulti(false);
        trustStorePasswordProperties.setGroup("Trust Store");
        trustStorePassword.setProperties(trustStorePasswordProperties);
        attrs.add(trustStorePassword);

        return attrs;
    }

    private boolean validateSofKeyStoreAttributes(List<RequestAttributeDto> attributes) {
        AttributeDefinitionUtils.validateAttributes(getSofKeyStoreAttributes(), attributes);

        try {
            String keyStoreBase64 = AttributeDefinitionUtils.getAttributeContentValue(ATTRIBUTE_KEYSTORE, attributes, FileAttributeContent.class).get(0).getData().getContent();
            byte[] keyStoreBytes = Base64.getDecoder().decode(keyStoreBase64);

            String keyStoreType = AttributeDefinitionUtils.getAttributeContentValue(ATTRIBUTE_KEYSTORE_TYPE, attributes, StringAttributeContent.class).get(0).getData();

            String keyStorePassword = AttributeDefinitionUtils.getAttributeContentValue(ATTRIBUTE_KEYSTORE_PASSWORD, attributes, SecretAttributeContent.class).get(0).getData().getSecret();

            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(new ByteArrayInputStream(keyStoreBytes), keyStorePassword.toCharArray());
            logger.info("Key store attribute successfully validated. Given key store contains: {}", keyStore.aliases());

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ValidationException(ValidationError.create(e.getMessage()));
        }

        try {
            List<FileAttributeContent> trustStoreBase64 = AttributeDefinitionUtils.getAttributeContentValue(ATTRIBUTE_TRUSTSTORE, attributes, FileAttributeContent.class);

            List<StringAttributeContent> trustStoreType = AttributeDefinitionUtils.getAttributeContentValue(ATTRIBUTE_TRUSTSTORE_TYPE, attributes, StringAttributeContent.class);

            List<SecretAttributeContent> trustStorePassword = AttributeDefinitionUtils.getAttributeContentValue(ATTRIBUTE_TRUSTSTORE_PASSWORD, attributes, SecretAttributeContent.class);

            if((trustStoreBase64 != null && !trustStoreBase64.isEmpty() && trustStoreBase64.get(0).getData() != null && trustStoreBase64.get(0).getData().getContent() != null)
                    || (trustStoreType != null && !trustStoreType.isEmpty() && trustStoreType.get(0).getData() != null)
                    || (trustStorePassword != null && !trustStorePassword.isEmpty() && trustStorePassword.get(0).getData() != null)) {
                if(StringUtils.isAnyBlank(trustStoreBase64.get(0).getData().getContent(), trustStoreType.get(0).getData(), trustStorePassword.get(0).getData().getSecret())){
                    throw new ValidationException(ValidationError.create("All attributes required for truststore must be provided"));
                }
            }

            if (!StringUtils.isAnyBlank(trustStoreBase64.get(0).getData().getContent(), trustStoreType.get(0).getData(), trustStorePassword.get(0).getData().getSecret())) {
                byte[] trustStoreBytes = Base64.getDecoder().decode(trustStoreBase64.get(0).getData().getContent());
                KeyStore trustStore = KeyStore.getInstance(trustStoreType.get(0).getData());
                trustStore.load(new ByteArrayInputStream(trustStoreBytes), trustStorePassword.get(0).getData().getSecret().toCharArray());
                logger.info("Trust store attribute successfully validated. Given trust store contains: {}", trustStore.aliases());
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ValidationException(ValidationError.create(e.getMessage()));
        }

        return true;
    }

    private List<BaseAttribute> getBasicAttributes() {
        List<BaseAttribute> attrs = new ArrayList<>();

        DataAttribute username = new DataAttribute();
        username.setUuid("fe2d6d35-fb3d-4ea0-9f0b-7e39be93beeb");
        username.setName(ATTRIBUTE_USERNAME);
        username.setDescription("Username");
        username.setType(AttributeType.DATA);
        username.setContentType(AttributeContentType.STRING);
        AttributeProperties usernameProperties = new AttributeProperties();
        usernameProperties.setLabel(ATTRIBUTE_USERNAME_LABEL);
        usernameProperties.setRequired(true);
        usernameProperties.setReadOnly(false);
        usernameProperties.setVisible(true);
        usernameProperties.setList(false);
        usernameProperties.setMulti(false);
        usernameProperties.setGroup("Basic");
        username.setProperties(usernameProperties);
        attrs.add(username);

        DataAttribute password = new DataAttribute();
        password.setUuid("04506d45-c865-4ddc-b6fc-117ee5d5c8e7");
        password.setName(ATTRIBUTE_PASSWORD);
        password.setDescription("Password");
        password.setType(AttributeType.DATA);
        password.setContentType(AttributeContentType.SECRET);
        AttributeProperties passwordProperties = new AttributeProperties();
        passwordProperties.setLabel(ATTRIBUTE_PASSWORD_LABEL);
        passwordProperties.setRequired(true);
        passwordProperties.setReadOnly(false);
        passwordProperties.setVisible(true);
        passwordProperties.setList(false);
        passwordProperties.setMulti(false);
        passwordProperties.setGroup("Basic");
        password.setProperties(passwordProperties);
        attrs.add(password);

        return attrs;
    }

    private boolean validateBasicAttributes(List<RequestAttributeDto> attributes) {
        AttributeDefinitionUtils.validateAttributes(getBasicAttributes(), attributes);
        return true;
    }

    private List<BaseAttribute> getApiKeyAttributes() {
        List<BaseAttribute> attrs = new ArrayList<>();

        DataAttribute apiKey = new DataAttribute();
        apiKey.setUuid("aac5c2d5-5dc3-4ddb-9dfa-3d76b99135f8");
        apiKey.setName(ATTRIBUTE_APIKEY);
        apiKey.setDescription("API Key");
        apiKey.setType(AttributeType.DATA);
        apiKey.setContentType(AttributeContentType.SECRET);
        AttributeProperties apiKeyProperties = new AttributeProperties();
        apiKeyProperties.setLabel(ATTRIBUTE_APIKEY_LABEL);
        apiKeyProperties.setRequired(true);
        apiKeyProperties.setReadOnly(false);
        apiKeyProperties.setVisible(true);
        apiKeyProperties.setList(false);
        apiKeyProperties.setMulti(false);
        apiKeyProperties.setGroup("API Key");
        apiKey.setProperties(apiKeyProperties);
        attrs.add(apiKey);

        return attrs;
    }

    private boolean validateApiKeyAttributes(List<RequestAttributeDto> attributes) {
        AttributeDefinitionUtils.validateAttributes(getApiKeyAttributes(), attributes);
        return true;
    }
}