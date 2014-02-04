package com.ee.midas.soaktest

def configURL = new File("Config.groovy").toURI().toURL()
def config = new ConfigSlurper().parse(configURL)

def deltasDir = config.soakdata.deltas.baseDir
def expansions = config.soakdata.deltas.expansions
def contractions = config.soakdata.deltas.contractions
def pathSeparator = File.separator

expansions.each {
    deltaName, deltaContent ->
        createDeltaFile("${deltasDir}${pathSeparator}expansion${pathSeparator}${deltaName}", deltaContent)
}

contractions.each {
    deltaName, deltaContent ->
        createDeltaFile("${deltasDir}${pathSeparator}contraction${pathSeparator}${deltaName}", deltaContent)
}

def createDeltaFile(absoluteFileName, content) {
    def deltaFile = new File(absoluteFileName)
    println("creating delta: ${absoluteFileName}")
    !deltaFile.exists()?: deltaFile.delete()
    deltaFile.createNewFile()
    deltaFile.withWriter { out ->
        content.eachLine {
            out.println(it.stripIndent())
        }
    }
}

println(expansions)
println(contractions)