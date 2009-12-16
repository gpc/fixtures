package grails.plugin.fixtures.buildtestdata

class BuildTestDataBeanDefinitionTranslator {

    def grailsApplication
    
    static protected BEAN_DEFINING_NO_PROPERTIES_SIGNATURE = [Class]
    static protected BEAN_DEFINING_CLOSURE_ONLY_SIGNATURE = [Class, Closure]
    static protected BEAN_DEFINING_MAP_CONSTRUCTOR_ONLY_SIGNATURE = [Map, Class]
    static protected BEAN_DEFINING_LITERAL_MAP_CONSTRUCTOR = [Class, Map]
    static protected BEAN_DEFINING_MAP_CONSTRUCTOR_AND_CLOSURE_SIGNATURE = [Map, Class, Closure]
    static protected BEAN_DEFINING_LITERAL_MAP_CONSTRUCTOR_AND_CLOSURE_SIGNATURE = [Class, Map, Closure]
     
    boolean translate(builder, name, Object[] args) {
        def signature = args*.getClass()
        
        def isSignature = { targetSignature ->
            println "checking signature of $name($args) $targetSignature matches $signature"
            if (signature.size() == targetSignature.size()) {
                (0..(targetSignature.size() - 1)).every { targetSignature[it].isAssignableFrom(signature[it]) }
            } else {
                false
            }
        }

        if (isSignature(BEAN_DEFINING_NO_PROPERTIES_SIGNATURE)) {
            translate(builder, name, args[0], null, null)
        } else if (isSignature(BEAN_DEFINING_CLOSURE_ONLY_SIGNATURE)) {
            translate(builder, name, args[0], null, args[1])
        } else if (isSignature(BEAN_DEFINING_LITERAL_MAP_CONSTRUCTOR)) {
            translate(builder, name, args[0], args[1], null)
        } else if (isSignature(BEAN_DEFINING_MAP_CONSTRUCTOR_ONLY_SIGNATURE)) {
            translate(builder, name, args[1], args[0], null)
        } else if (isSignature(BEAN_DEFINING_MAP_CONSTRUCTOR_AND_CLOSURE_SIGNATURE)) {
            translate(builder, name, args[1], args[0], args[2])
        } else if (isSignature(BEAN_DEFINING_LITERAL_MAP_CONSTRUCTOR_AND_CLOSURE_SIGNATURE)) {
            translate(builder, name, args[0], args[1], args[2])
        } else {
            false
        }
    }

    protected translate(builder, name, domainClass, overridePropertiesMap, overridePropertiesClosure) {
        if (grailsApplication.isDomainClass(domainClass)) {
            
            def overridePropertiesBuilder = new OverridePropertyMapBuilder(
                builder, 
                overridePropertiesMap, 
                overridePropertiesClosure
            )
            
            builder.with {
                noBuild {
                    "$name"(BuildTestDataUtilisingFactoryBean) {
                        delegate.domainClass = domainClass
                        delegate.overrideProperties = overridePropertiesBuilder.build()
                    }
                }
            }
            
            true
        } else {
            false
        }
    }
}