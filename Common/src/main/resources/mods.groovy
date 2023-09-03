/*
 * Copyright (C) 2023 Luke Bemish and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

ModsDotGroovy.make {
    modLoader = 'javafml'
    loaderVersion = '[47,)'
    issueTrackerUrl = 'https://github.com/lukebemishprojects/WorldgenFlexiblifier/issues'
    license = 'LGPL-3.0-or-later'

    mod {
        modId = this.buildProperties.mod_id
        displayName = this.buildProperties.mod_name
        version = this.version
        onQuilt {
            group = this.group
        }
        displayUrl = 'https://github.com/lukebemishprojects/WorldgenFlexiblifier'
        contact.sources = 'https://github.com/lukebemishprojects/WorldgenFlexiblifier'
        author 'Luke Bemish'
        description = "Removes hardcoding from various data-driven components of minecraft worldgen"

        dependencies {
            onForge {
                forge = ">=${this.buildProperties.forge_compat}"
            }
            minecraft = this.minecraftVersionRange
        }

        onFabric {
            entrypoints {
                main = [
                    'dev.lukebemish.worldgenflexiblifier.impl.fabriquilt.FabriQuiltInit'
                ]
            }
        }
    }
    onFabric {
        mixin = [
                'mixin.worldgenflexiblifier.json'
        ]
    }
}
