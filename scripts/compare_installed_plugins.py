import io
import os
import re
import sys


def extract_plugin_versions(file_path):
    pattern = re.compile(r'(?P<plugin>[\w-]+): (?P<version>[0-9.]+)$')
    plugin_version_dict = {}
    with io.open(file_path, 'r') as plugin_file:
        for line in plugin_file.readlines():
            clean_line = line.strip()
            if re.search(pattern, clean_line):
                plugin = re.search(pattern, line).group('plugin')
                version = re.search(pattern, line).group('version')
                plugin_version_dict[plugin] = version
    return plugin_version_dict


def get_new_installs(base_plugins, new_plugins):
    new_installs = {}
    for p, v in new_plugins.items():
        if p not in base_plugins.keys():
            new_installs[p] = v
    return new_installs

def get_removes(base_plugins, new_plugins):
    removes = {}
    for p, v in base_plugins.items():
        if p not in new_plugins.keys():
            removes[p] = v
    return removes

def get_updates(base_plugins, new_plugins):
    updates = {}
    for p, v in new_plugins.items():
        if p in base_plugins.keys() and base_plugins[p] != v:
            updates[p] = "{} -> {}".format(base_plugins[p], v)
    return updates



def main():
    try:
        base_installed_plugin_file = sys.argv[1]
        new_installed_plugin_file = sys.argv[2]
    except IndexError:
        print('Need to supply two files!')
        sys.exit(1)

    base_plugins = extract_plugin_versions(base_installed_plugin_file)
    new_plugins = extract_plugin_versions(new_installed_plugin_file)

    new_installs = get_new_installs(base_plugins, new_plugins)
    removes = get_removes(base_plugins, new_plugins)
    updates = get_updates(base_plugins, new_plugins)

    print('The following plugins will be installed:')
    for p, v in new_installs.items():
        print('{}: {}'.format(p, v))
    print("")
    print('The following plugins will be removed:')
    for p, v in removes.items():
        print('{}: {}'.format(p, v))
    print("")
    print('The following plugins will be updated:')
    for p, update_string in updates.items():
        print('{}: {}'.format(p, update_string))


if __name__ == "__main__":
    main()
