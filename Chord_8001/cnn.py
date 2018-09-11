# Just disables the warning, doesn't enable AVX/FMA
import os
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '2'

import numpy as np
import pandas as pd
import cv2

# read bounding box file
bb_path = "filtered_cars_train_annos.csv"
pre_train_anno = pd.read_csv(bb_path, header = None)

#pre_train_car = np.asarray(pre_train_anno.iloc[:, :-2]) # bounding coordinate
train_label = np.asarray(pre_train_anno.iloc[:, -2])
print(train_label)
img_list = np.asarray(pre_train_anno.iloc[:, -1])


num = 0
train_car = []
while (num < len(img_list)):
    image_name = img_list[num]
#     print(image_name)
    img = cv2.imread('resize_cars_train/'+image_name, cv2.IMREAD_GRAYSCALE)
    train_car.append(img)
    num += 1

X_train = np.array(train_car)
print(np.array(train_car).shape)
print(train_label.shape)
y_train = train_label
# print(y_train)

classID_left = np.genfromtxt('classid_left.txt', delimiter=',')
classID_left = classID_left.astype('int')

# print(dict)
# print(type(classID_left))
# print(classID_left)

# map class id to 0~48
dict = {value: key for (key, value) in enumerate(classID_left)}
converted_y_train = []
for cl in y_train:
    converted_y_train.append(dict.get(cl))

converted_y_train = np.array(converted_y_train)
# print(converted_y_train)

# convert to 1-of-c output encoding
from keras.utils import np_utils
Y_train = np_utils.to_categorical(converted_y_train, 5)
#print(Y_train)

#
#
#
#
#---- Construct network ----
from keras.models import Sequential
from keras.layers import Dense, Activation, Convolution2D, Flatten,Convolution1D

# declare a Sequential model
model = Sequential()

# Add the first Convolutional layer
from keras.layers import Conv2D, MaxPooling2D, Conv1D,MaxPooling1D
model.add(Conv1D(
    input_shape=(128,256),
    filters=16, # number of filters
    kernel_size=2, # 2x2 filter
    strides=1,
    padding='same', # the size of image remains the same
))
model.add(Activation('relu'))

# Add the first max pooling
# info losses occur during convolution, pooling solves this problen and picks up useful info for the next layer.
# reduce amount of parameters => reduce rates of overfitting
model.add(MaxPooling1D(
    pool_size=2, # 2x2
    strides=2,
    padding='same'
    # data_format='channels_first'
))
# (32, 4, 4)

# Add the second Convolutional layer
model.add(Conv1D( 32, 3, strides=1, padding='same'))
model.add(Activation('relu'))

# Add the second max pooling
model.add(MaxPooling1D( 2, 2, padding='same'))
# (64, 7, 7)

# fully connected network
model.add(Flatten())
model.add(Dense(256))
model.add(Activation('relu'))
# output: 49 neurons
model.add(Dense(5))
model.add(Activation('softmax'))


# --- Choose optimizer ---
from keras.optimizers import SGD, Adam, RMSprop, Adagrad

# update = -lr*gradient + m*last_update
# 1/t decay -> lr = lr / (1+ decay*t), t: number of done updates

# sgd = SGD(lr=0.01, momentum=0.2, decay=1e-6, nesterov=False)
sgd = SGD(lr=0.001, momentum=2, nesterov=True)
rmsprop = RMSprop(lr=0.01, rho=0.9, epsilon=1e-08, decay=0.0)
adam = Adam(lr=0.01, beta_1=0.9, beta_2=0.999, epsilon=1e-08, decay=0.0)

# compile the model
model.compile(optimizer=sgd, loss='categorical_crossentropy', metrics=['accuracy'])

# specify early stopping
from keras.callbacks import EarlyStopping
# early_stopping = EarlyStopping(monitor='acc', min_delta=0.003, patience=2, verbose=0, mode='max')
early_stopping = EarlyStopping(monitor='loss', min_delta=0.005, patience=10)

# train the model
history = model.fit(X_train, Y_train, epochs=10, batch_size=16, callbacks=[early_stopping] )#,validation_split=0.2

#
#
# Result
loss, accuracy = model.evaluate(X_train, Y_train)
print("\ntrain loss: {}".format(loss))
print("\ntrain accuracy: {}".format(accuracy))
# loss, accuracy = model.evaluate(X_test, Y_test)
# print("\ntest loss: {}".format(loss))
# print("\ntest accuracy: {}".format(accuracy))
