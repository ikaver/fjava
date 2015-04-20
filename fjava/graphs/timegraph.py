import os
import sys

import re

import numpy as np
import matplotlib.pyplot as plt


def show_graph(path, stats):
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


def create_dir_if_necessary(dir_name):
    if not os.path.exists(dir_name):
        os.makedirs(dir_name)


def process_stats(dir_name):
    stats = {}


    create_dir_if_necessary(dir_name)
    dir_name = os.path.join(dir_name, 'time')
    create_dir_if_necessary(dir_name)

    lines = sys.stdin.readlines()

    for i in xrange(0, len(lines)/2):
        name_line = lines[2*i]
        stats_line = lines[2*i+1]

        name = name_line.split(":")[0].split(".")[1]
        stats_str = stats_line.split(",")

        for stat in stats_str:
            stat_tokens = stat.split(" ")
            if '[' in stat:
                stat_name = stat_tokens[1].split(":", 1)[0]
                stat_time = float(stat_tokens[2])
                stat_stddev = float(stat_tokens[4].split("]")[0])
            else:
                stat_name = stat_tokens[1].split(":", 1)[0]
                stat_time = float(stat_tokens[2])
                stat_stddev = 0.0
            if stat_name == 'time.total':
                continue
            if not stat_name in stats:
                stats[stat_name] = []

            print 'adding ', stat_name, name, stat_time, stat_stddev
            stats[stat_name].append((name, stat_time, stat_stddev))
    show_graph(dir_name, stats)

if __name__ == '__main__':
    if len(sys.argv) < 2:
        print 'Usage: python statsgraph.py <OUTPUT_DIR_NAME>'
    process_stats(sys.argv[1])
