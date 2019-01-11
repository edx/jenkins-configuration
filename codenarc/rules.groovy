ruleset {

    ruleset('rulesets/basic.xml')

    ruleset('rulesets/braces.xml')

    ruleset('rulesets/formatting.xml') {
        exclude 'SpaceAroundMapEntryColon'
        exclude 'SpaceAfterClosingBrace'
    }

    ruleset('rulesets/generic.xml')

    ruleset('rulesets/imports.xml') {
        exclude 'NoWildcardImports'
        exclude 'MisorderedStaticImports'
    }

}
