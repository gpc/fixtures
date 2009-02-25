eventWarStart = { warName ->
    def stagingFixtures = "${grailsSettings.projectWorkDir}/staging/fixtures"
    ant.mkdir(dir: stagingFixtures)
    ant.copy(todir: stagingFixtures) {
        fileset(dir: "${grailsSettings.baseDir}/fixtures")
    }
}