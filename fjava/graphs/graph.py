import os
import sys

import re

import numpy as np
import matplotlib.pyplot as plt
import fileinput


def show_graph(path, stats):
    if len(stats) == 0:
        return

    for key in stats.keys():

        labels = map(lambda x: x[0], stats[key])
        values = map(lambda x: x[1], stats[key])

        ind = np.arange(len(labels))  # x locations
        width = 0.35  # width of bars

        fig, ax = plt.subplots()
        ax.set_title(key)
        ax.bar(ind, values, width, align='center')

        ax.set_xticks(ind)
        ax.set_xticklabels(labels)
        plt.savefig(path + '/' + key + '.png')


def create_dir_if_necessary(dir_name):
    if not os.path.exists(dir_name):
        os.makedirs(dir_name)


def process_stats(dir_name, regex):
    stats = {}

    rexp = re.compile(regex)

    create_dir_if_necessary(dir_name)
    run_counter = 0
    dir_name = os.path.join(dir_name, 'run-')

    for line in sys.stdin:
        if line.startswith(' Stats for run'):
            if run_counter > 0:
                create_dir_if_necessary(dir_name + str(run_counter))
                show_graph(dir_name + str(run_counter), stats)
                stats = {}
            run_counter += 1
        elif rexp.match(regex):
            tokens = line.split(':')
            id_tokens = tokens[0].split("#")
            category_id = id_tokens[0]
            num = 0
            if len(id_tokens) > 1:
                num = id_tokens[1]
            count = int(tokens[1])
            if not category_id in stats:
                stats[category_id] = []
            stats[category_id].append((num, count))


if __name__ == '__main__':
    if len(sys.argv) < 3:
        print 'Usage: python graph.py <OUTPUT_DIR_NAME> <REGEX>'
    process_stats(sys.argv[1], sys.argv[2])
