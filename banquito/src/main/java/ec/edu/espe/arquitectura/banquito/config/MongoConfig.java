package ec.edu.espe.arquitectura.banquito.config;

import java.math.BigDecimal;
import java.util.Arrays;

import org.bson.types.Decimal128;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    
    
    @Value("${spring.data.mongodb.uri}")
    private String uri;
    private String databaseName;
    public String obtenerNombreBaseDeDatosDesdeURL(String urlConexion) {
        int indexUltimaBarra = urlConexion.lastIndexOf("/");
        if (indexUltimaBarra >= 0 && indexUltimaBarra < urlConexion.length() - 1) {
            return urlConexion.substring(indexUltimaBarra + 1);
        } else {
            // Manejar caso de URL inválida o sin nombre de base de datos
            return null;
        }
    }
    

    @Override
    protected String getDatabaseName() {
        this.databaseName=obtenerNombreBaseDeDatosDesdeURL(uri);
        return this.databaseName;
    }

    @Override
    public boolean autoIndexCreation() {
        return true;
    }

    @Bean
    @Override
    public MappingMongoConverter mappingMongoConverter(MongoDatabaseFactory databaseFactory,
            MongoCustomConversions customConversions, MongoMappingContext mappingContext) {
        MappingMongoConverter converter = super.mappingMongoConverter(databaseFactory, customConversions,
                mappingContext);
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return converter;
    }

    /**
     * Inject a CustomConversions bean to overwrite the default mapping of
     * BigDecimal.
     *
     * @return a new instance of CustomConversons
     */
    @Bean
    @Override
    public MongoCustomConversions customConversions() {
        Converter<Decimal128, BigDecimal> decimal128ToBigDecimal = new Converter<>() {
            @Override
            public BigDecimal convert(Decimal128 s) {
                return s == null ? null : s.bigDecimalValue();
            }
        };

        Converter<BigDecimal, Decimal128> bigDecimalToDecimal128 = new Converter<>() {
            @Override
            public Decimal128 convert(BigDecimal s) {
                return s == null ? null : new Decimal128(s);
            }
        };

        return new MongoCustomConversions(Arrays.asList(decimal128ToBigDecimal, bigDecimalToDecimal128));
    }

}
