eventWarStart = { warName ->

	def stagingFixtures
	if (grailsSettings.metaClass.hasProperty(grailsSettings, 'projectWarExplodedDir')) {
		stagingFixtures = grailsSettings.projectWarExplodedDir.path
	} else {
		stagingFixtures = grailsSettings.projectWorkDir.path + '/staging'
	}
	stagingFixtures += '/fixtures'
    createConfig()

    def fixturesDir = new File(grailsSettings.baseDir, (config.grails.plugin.fixtures.directory) ?: "fixtures")

	if (fixturesDir.exists()) {
		ant.mkdir(dir: stagingFixtures)
		ant.copy(todir: stagingFixtures) {
			fileset(dir: fixturesDir)
		}
	}
}

eventRefdocsStart = {
	if (grailsAppName == 'grails-fixtures') {
		createConfig()
		classLoader.loadClass('grails.doc.DocEngine').ALIAS.putAll(config.grails.doc.alias)
	}
}
