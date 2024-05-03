package codegen;

import java.util.List;
import org.jooq.codegen.GenerationTool;
import org.jooq.meta.jaxb.Configuration;
import org.jooq.meta.jaxb.Database;
import org.jooq.meta.jaxb.ForcedType;
import org.jooq.meta.jaxb.Generate;
import org.jooq.meta.jaxb.Generator;
import org.jooq.meta.jaxb.Property;
import org.jooq.meta.jaxb.Target;

@SuppressWarnings("UncommentedMain")
public class JooqCodegen {
    public static void main(String[] args) throws Exception {
        List<Property> properties = List.of(
            new Property().withKey("rootPath").withValue("migrations"),
            new Property().withKey("scripts").withValue("master.xml")
        );

        Target target = new Target()
            .withPackageName("edu.java.scrapper.domain.jooq")
            .withDirectory("scrapper/src/main/java");

        ForcedType forceType = new ForcedType()
            .withName(".*")
            .withExpression("^.*$");

        Database database = new Database()
            .withForcedTypes(forceType)
            .withName("org.jooq.meta.extensions.liquibase.LiquibaseDatabase")
            .withProperties(properties);

        Generate generate = new Generate()
            .withGeneratedAnnotation(false)
            .withNullableAnnotation(true)
            .withNullableAnnotationType("org.jetbrains.annotations.Nullable")
            .withNonnullAnnotation(true)
            .withNonnullAnnotationType("org.jetbrains.annotations.NotNull")
            .withJpaAnnotations(false)
            .withValidationAnnotations(true)
            .withSpringAnnotations(true)
            .withConstructorPropertiesAnnotation(true)
            .withConstructorPropertiesAnnotationOnPojos(true)
            .withConstructorPropertiesAnnotationOnRecords(true)
            .withFluentSetters(false)
            .withDaos(false)
            .withPojos(true)
            .withImmutablePojos(false);

        Generator generator = new Generator()
            .withGenerate(generate)
            .withDatabase(database)
            .withTarget(target);

        Configuration configuration = new Configuration()
            .withGenerator(generator);

        GenerationTool.generate(configuration);
    }

    private JooqCodegen() {
    }
}
