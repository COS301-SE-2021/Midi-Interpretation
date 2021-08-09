import array as ar
import math
import os
import random
import sys
import numpy as np

"""
    Method to convert a midi file <file> to a feature vector, where each entry is a byte in the file. The file is truncated
    after <max_file_size> kilobytes
"""

def file_to_features(file, max_file_size):
    # Read in a MIDI file as byte stream
    fb = ar.array('B')
    try:
        fb.fromfile(file, max_file_size)
    except EOFError as e:
        pass
    finally:
        file.close()

    # Append to byte array until of maximum length
    while len(fb) < max_file_size:
        fb.append(0)

    # return a feature vector
    return np.array(fb).reshape(1, len(fb))


"""
    Method to generate a dataset file and classification file, based on input passed in through system arguments.
    Arguments are:
        - The maximum number of dataset features (in Kb per file)
        - The path of the directory containing test data
        - The maximum number of genres (classification classes). If more than necessary are provided, only those present
        will be used.
        - The file containing genre classifications in the following format:
                <genre> ; <class> ; <weight>
        - The maximum number of records (tracks) to produce. If more than necessary are provided, only those present
        will be used.
        - The file containing track classifications in the following tab-seperated format:
                <track name>    <artist>    <source link>   "[<genre>,...,<genre>]"
    
    Produces two files in the execution directory: 
        - dataset.npy : contains the array of data with features along the rows
        - classifications.npy : contains the list of classifications corresponding to a row in the former
        
"""


def main():
    # set the file format : only checks for files of this format
    ff = ".mid"

    fs = None
    fd = None
    fc_name = None
    fg_name = None
    mg = None
    mt = None

    try:
        # Set the maximum file size, in bytes
        fs = int(sys.argv[1])
        fs = int(fs * math.pow(2, 10))

        # Set the directory to look for files
        fd = str(sys.argv[2]).replace("\\", "/")
        fd = fd if (fd[-1] == "/") else (fd + "/")

        # Set the max number of genres
        mg = max(int(sys.argv[3]), 0)

        # Get the genres from an input file
        fg_name = str(sys.argv[4])

        # Set the max number of training examples
        mt = max(int(sys.argv[5]), 0)

        # Get the classifications from an input file
        fc_name = str(sys.argv[6])


    except Exception:
        sys.exit("Usage is: python dataset_creator "
                 "<truncated feature size (kb)> "
                 "<directory containing training data> "
                 "<max no. of genres> "
                 "<genre classification text file> "
                 "<max no. of output records> "
                 "<track classification folder>")

    # list of all file in dir with appropriate format
    fl = [file for file in os.listdir(fd) if ff in file]

    try:
        fc = open(fc_name, "r")
        c = [s.split("\t") for s in fc.read().split("\n")[0:-1]]
        c = [{"track": d[0].strip("\""), "genres": d[3][1:-1].split(",")} for d in c]
        c_dash = [f['track'].replace(" ", "").replace(",", "") + ff for f in c]
    except Exception:
        sys.exit("Bad format in genre list")

    try:
        # Get the genres
        fg = open(fg_name, "r")
        g = fg.read().split('\n')[0:-1]
        g = [k.split(";") for k in g]
        g = [{"genre": m[0], "class": m[1], "weight": m[2]} for m in g]
        g_dash = [p['genre'] for p in g]

        mg = min(mg, len(g_dash))
        g_dash = g_dash[:mg]

    except Exception:
        sys.exit("Bad format in track list")

    for k in c:
        i = []
        for o in k['genres']:
            try:
                i.append(g_dash.index(o))
            except Exception:
                pass
        i.sort()
        k['genres'] = i

    # Initial dataset and classification
    X = None
    Y = None

    # Subset of Training data
    mt = min(mt, len(fl))

    # Generate random sample of data
    subset = random.sample(fl, mt)


    index = 0
    total = len(subset)

    if total == 0:
        sys.exit("No files to categorise")

    for fn in subset:
        index = index + 1
        print("Item {:^5} of {:^5} | [{:<20}] ".format(index, total, int((20 * index) / total) * "#"), end="")
        try:
            fn_comp = fn.replace(",", "")
            i = c_dash.index(fn_comp)

            print("[*]", end="\r")

            # Read file as byte stream
            m = file_to_features(open(fd + fn, "rb"), fs)

            # Get the classification from the list
            lg = c[i]['genres']

            # Add to existing dataset
            X = m if X is None else np.concatenate((X, m))

            # Add to classification array
            Y = [lg] if Y is None else Y + [lg]


        except ValueError:
            print("[x]", end="\r")
            continue
    print()

    # Save the dataset for later use
    np.save("training_testing_sets/dataset.npy", X)

    # Save the classifications for later use
    Y = np.array(Y, dtype=object)
    np.save("training_testing_sets/classifications.npy", Y)


if __name__ == "__main__":
    main()
