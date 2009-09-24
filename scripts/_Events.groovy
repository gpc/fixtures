eventWarStart = { warName ->
    
    def stagingFixtures
    if (grailsSettings.metaClass.hasProperty(grailsSettings, 'projectWarExplodedDir')) {
        stagingFixtures = grailsSettings.projectWarExplodedDir.path
    } else {
        stagingFixtures = grailsSettings.projectWorkDir.path + '/staging'
    }
    stagingFixtures += '/fixtures'
    
    ant.mkdir(dir: stagingFixtures)
    ant.copy(todir: stagingFixtures) {
        fileset(dir: "${grailsSettings.baseDir}/fixtures")
    }
}