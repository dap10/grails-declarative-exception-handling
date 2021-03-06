import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator
import org.codehaus.groovy.grails.commons.*
import grails.plugins.ctrlex.*

class DeclarativeExceptionHandlingGrailsPlugin {

    def version = "0.3"
    def grailsVersion = "1.3.5 > *"
    def dependsOn = [:]
    def pluginExcludes = [
		"grails-app/views/error.gsp",
		"**/demo/**"
    ]

	def loadAfter = [ "controllers", "urlMappings" ] 
	
	def watchedResources = [ 
		"file:./grails-app/conf/*UrlMappings.groovy",
		"file:./grails-app/conf/**/*UrlMappings.groovy",
		"file:./plugins/*/grails-app/conf/*UrlMappings.groovy",
		"file:./plugins/*/grails-app/conf/**/*UrlMappings.groovy"
	]
	
    def author = "Kim A. Betti"
    def authorEmail = "kim@developer-b.com"
    def title = "Exception mapping"
    def description = 'Declerative exception mapping'

    def documentation = "http://grails.org/plugin/controlled-exceptions"

    def doWithSpring = {
        exceptionMapper(ExceptionMapper)
		
		controlledExceptionHandler(ControlledExceptionHandler) {
			exceptionMapper = ref("exceptionMapper")
		}
		
		exceptionHandlerPostProcessor(ExceptionHandlerPostProcessor) {
			controlledExceptionHandler = ref("controlledExceptionHandler")
		}
    }

    def doWithApplicationContext = { applicationContext ->
		GrailsApplication grailsApplication = applicationContext.getBean("grailsApplication")
		ExceptionMapper exceptionMapper = applicationContext.getBean("exceptionMapper")
		readUrlMappings(grailsApplication, exceptionMapper)
    }

    def onChange = { event ->
        if (application.isArtefactOfType(UrlMappingsArtefactHandler.TYPE, event.source)) {
			ExceptionMapper mapper = event.ctx.getBean("exceptionMapper")
			GrailsApplication grailsApplication = event.ctx.getBean("grailsApplication")
			
			mapper.resetMappings()
			readUrlMappings(grailsApplication, mapper)
		}
    }
	
	private void readUrlMappings(GrailsApplication grailsApplication, ExceptionMapper mapper) {
		grailsApplication.getArtefacts(UrlMappingsArtefactHandler.TYPE).each { GrailsClass gc ->
			Class<?> urlMappingClass = gc.getClazz()
			if (GrailsClassUtils.isStaticProperty(urlMappingClass, "exceptionMappings"))
				mapper.readExceptionMapping urlMappingClass.exceptionMappings
		}
	}

	def doWithDynamicMethods = { ctx -> }
	def onConfigChange = { event -> }
	def doWithWebDescriptor = { xml -> }
	
}
