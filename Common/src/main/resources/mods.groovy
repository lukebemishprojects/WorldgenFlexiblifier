/*
 * Copyright (C) 2023 Luke Bemish, and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

ModsDotGroovy.make {
    modLoader = 'javafml'
    loaderVersion = '[47,)'
    issueTrackerUrl = 'https://github.com/lukebemish/WorldgenFlexiblifier/issues'
    license = 'LGPL-3.0-or-later'

    mod {
        modId = this.buildProperties.mod_id
        displayName = this.buildProperties.mod_name
        version = this.version
        onQuilt {
            group = this.group
        }
        displayUrl = 'https://github.com/lukebemish/WorldgenFlexiblifier'
        contact.sources = 'https://github.com/lukebemish/WorldgenFlexiblifier'
        author 'Luke Bemish'
        description = "Removes hardcoding from various data-driven components of minecraft worldgen"

        dependencies {
            onForge {
                forge = ">=${this.forgeVersion}"
            }
            minecraft = this.minecraftVersionRange
        }
    }
    onFabric {
        mixin = [
                'mixin.worldgenflexiblifier.json'
        ]
    }
}
