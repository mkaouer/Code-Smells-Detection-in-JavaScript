modshogun

addpath('tools');
label_train_multiclass=load_matrix('../data/label_train_multiclass.dat');
fm_train_real=load_matrix('../data/fm_train_real.dat');
fm_test_real=load_matrix('../data/fm_test_real.dat');

disp('MulticlassLibSVM')

feats_train=RealFeatures(fm_train_real);
feats_test=RealFeatures(fm_test_real);
width=2.1;
kernel=GaussianKernel(feats_train, feats_train, width);

C=1.2;
epsilon=1e-5;
num_threads=8;
labels=MulticlassLabels(label_train_multiclass);

svm=MulticlassLibSVM(C, kernel, labels);
svm.set_epsilon(epsilon);
svm.parallel.set_num_threads(num_threads);
svm.train();

kernel.init(feats_train, feats_test);
svm.apply().get_labels();
