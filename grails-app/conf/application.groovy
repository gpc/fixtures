if(System.getenv('TRAVIS_BRANCH')) {
  grails.project.repos.grailsCentral.username = System.getenv("GRAILS_CENTRAL_USERNAME")
  grails.project.repos.grailsCentral.password = System.getenv("GRAILS_CENTRAL_PASSWORD")
}


log4j = { //todo
  debug 'grails.plugin.fixtures'
  debug 'grails.app.fixtures' //log added to fixtures
}

grails {
  doc {
    title = "Grails Fixtures Plugin"
    subtitle = "Load complex domain data via a convenient DSL"
    authors = "Grails Plugin Collective"
    copyright = "Copies of this document may be made for your own use and for distribution to others, provided that you do not charge any fee for such copies and further provided that each copy contains this Copyright Notice, whether distributed in print or electronically."
    footer = "Developed by the <a href='http://gpc.github.com'>Grails Plugin Collective</a>"
    alias {
      intro = "1. Introduction"
      btd = "2.2 Build-Test-Data Integration"
      loading = "3. Loading Fixtures"
      stacking = "5.2 Stacking"
    }
  }
}