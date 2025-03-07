/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import java.util.List;
import lombok.val;

@AnalyzeClasses(
    packages = "com.sportradar.unifiedodds.sdk",
    importOptions = ImportOption.DoNotIncludeTests.class
)
@SuppressWarnings({ "ConstantName", "DeclarationOrder" })
class PublicInterfaceArchUnitTest {

    private static final String[] PUBLIC_PACKAGES = {
        "com.sportradar.unifiedodds.sdk",
        "com.sportradar.unifiedodds.sdk.cfg",
        "com.sportradar.unifiedodds.sdk.managers",
        "com.sportradar.unifiedodds.sdk.exceptions",
        "com.sportradar.unifiedodds.sdk.extended",
        "com.sportradar.unifiedodds.sdk.entities",
        "com.sportradar.unifiedodds.sdk.oddsentities",
        "com.sportradar.utils",
    };

    private static final String INTERNAL_PACKAGE = "com.sportradar.unifiedodds.sdk.internal..";

    static DescribedPredicate<List<JavaClass>> ofClassesFromInternalPackage() {
        return new DescribedPredicate<List<JavaClass>>("parameter types are not from internal package") {
            @Override
            public boolean test(List<JavaClass> javaClasses) {
                if (javaClasses.isEmpty()) {
                    return false;
                }
                val predicate = ofClassFromInternalPackage();
                return javaClasses.stream().allMatch(predicate);
            }
        };
    }

    static DescribedPredicate<JavaClass> ofClassFromInternalPackage() {
        return resideInAnyPackage(INTERNAL_PACKAGE);
    }

    @ArchTest
    static final ArchRule entitiesShouldBeInterfacesOrEnums = classes()
        .that()
        .resideInAnyPackage(
            "com.sportradar.unifiedodds.sdk.entities",
            "com.sportradar.unifiedodds.sdk.oddsentities"
        )
        .should()
        .beInterfaces()
        .orShould()
        .beEnums()
        .orShould()
        .beInnerClasses()
        .allowEmptyShould(true);

    @ArchTest
    static final ArchRule publicMethodsReturnTypesShouldNotDependOnInternalClasses = ArchRuleDefinition
        .methods()
        .that()
        .arePublic()
        .and()
        .areDeclaredInClassesThat()
        .resideInAnyPackage(PUBLIC_PACKAGES)
        .and()
        .doNotHaveFullName("com.sportradar.unifiedodds.sdk.UofSdkForReplay.getReplayManager()")
        .should()
        .notHaveRawReturnType(ofClassFromInternalPackage());

    @ArchTest
    static final ArchRule publicMethodsParameterTypesShouldNotDependOnInternalClasses = ArchRuleDefinition
        .methods()
        .that()
        .arePublic()
        .and()
        .areDeclaredInClassesThat()
        .resideInAnyPackage(PUBLIC_PACKAGES)
        .should()
        .notHaveRawParameterTypes(ofClassesFromInternalPackage());
}
