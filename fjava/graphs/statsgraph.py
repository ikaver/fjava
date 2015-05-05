import os
import sys

import re

import numpy as np
import matplotlib.pyplot as plt


def show_graph(path, stats, grouped_stats):
    for key in stats.keys():

        labels = map(lambda x: x[0], stats[key])  # deque id
        values = map(lambda x: x[1], stats[key])  # value

        ind = np.arange(len(labels))  # x locations
        width = 0.35  # width of bars

        fig, ax = plt.subplots()
        ax.set_title(key)
        ax.bar(ind, values, width, align='center')

        ax.set_xticks(ind)
        ax.set_xticklabels(labels)
        plt.savefig(path + '/' + key + '.png')

    colors = ['r', 'b', 'y', 'g', 'p']
    for group in grouped_stats.keys():

        stats_in_group = grouped_stats[group].keys()

        fig, ax = plt.subplots()
        ax.set_title(group)

        any_value = grouped_stats[group][stats_in_group[0]]
        any_value_labels = map(lambda x: x[0], any_value)
        ind = np.arange(len(any_value))  # x locations
        width = 0.35 / len(stats_in_group)

        width_sum = 0
        bars = []
        legend = []
        color_idx = 0
        for key in stats_in_group:
            labels = map(lambda x: x[0], grouped_stats[group][key])
            values = map(lambda x: x[1], grouped_stats[group][key])
            bars.append(ax.bar(ind+width_sum, values, width, align='center', color=colors[color_idx]))
            width_sum += width
            color_idx += 1
            legend.append(key)

        ax.set_xticks(ind+width/len(any_value_labels))
        ax.set_xticklabels(any_value_labels)
        ax.legend(bars, legend)
        plt.savefig(path + '/' + key + '.png')


def create_dir_if_necessary(dir_name):
    if not os.path.exists(dir_name):
        os.makedirs(dir_name)


def process_stats(dir_name, regex):
    stats = {}
    grouped_stats = {}

    rexp = re.compile(regex)

    create_dir_if_necessary(dir_name)
    run_counter = 0
    dir_name = os.path.join(dir_name, 'run-')

    for line in sys.stdin:
        if line.startswith('Stats for run'):
            if run_counter > 0:
                create_dir_if_necessary(dir_name + str(run_counter))
                show_graph(dir_name + str(run_counter), stats, grouped_stats)
                stats = {}
                grouped_stats = {}
            run_counter += 1
        elif rexp.match(regex):
            tokens = line.split(':')
            print tokens
            id_tokens = tokens[0].split("#")
            statistic_id = id_tokens[0]
            group_id = id_tokens[1]
            num = 0


            if len(id_tokens) > 1:
                num = id_tokens[2]
            count = int(tokens[1])

            if len(group_id) > 0:
                if not group_id in grouped_stats:
                    grouped_stats[group_id] = {}
                if not statistic_id in grouped_stats[group_id]:
                    grouped_stats[group_id][statistic_id] = []
                grouped_stats[group_id][statistic_id].append((num, count))
            else:
                if not statistic_id in stats:
                    stats[statistic_id] = []
                stats[statistic_id].append((num, count))


if __name__ == '__main__':
    if len(sys.argv) < 3:
        print 'Usage: python statsgraph.py <OUTPUT_DIR_NAME> <REGEX>'
    process_stats(sys.argv[1], sys.argv[2])
