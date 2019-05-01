//package com.postpc.nisha.storyline;
//
//
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.util.Log;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.OutputStream;
//import java.util.ArrayList;
//
//public class GifCreationThread extends Thread{
//
//    public GifCreationThread() {}
//
//    @Override
//    /**
//     * create a gif from given images (the gif is created from thumbnails of the images).
//     * since we create thumbnail for each input image, it is important that all the images will have
//     * originally the same resolution, so the created thumbnails will keep the original image aspect
//     * ratio. for postpc, images should be all shot in the same phone, or in different phones but
//     * with the same image resolution
//     */
//    public void run() {
//        createGif();
//    }
//
//    public void createGif(){
//        //TODO: write func that selects images to create gif from!!!!!!!!!!!!!!!!!!
//
//        ArrayList<String> thumbnailsPaths = new ArrayList<>();
//        try {
//            AnimatedGIFWriter writer = new AnimatedGIFWriter(false);
//            OutputStream gif = new FileOutputStream("/sdcard/media/TEST.gif");
////            OutputStream gif = new FileOutputStream("/sdcard/TEST.gif");
//            ArrayList<Bitmap> gifImages = new ArrayList<Bitmap>();
//            for (int i = 0; i < 10; i++) {
//                String sourceJpgDirectoryPath = "/sdcard/media/";
////                String sourceJpgDirectoryPath = "/sdcard/";
//                String inFilePath = sourceJpgDirectoryPath + Integer.toString(i + 1) + ".jpg";
////                String inFilePath = sourceJpgDirectoryPath + "Img0000" + Integer.toString(i+1) + ".jpg";
//
//                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
////                Bitmap tempBitmapForSize = BitmapFactory.decodeFile(inFilePath, bitmapOptions);
////                int height = tempBitmapForSize.getHeight();
////                int width = tempBitmapForSize.getWidth();
//                String imageThumbnailPath = createThumbnail(inFilePath, i);
//                thumbnailsPaths.add(imageThumbnailPath);
//                Bitmap imageThumbnail = BitmapFactory.decodeFile(imageThumbnailPath, bitmapOptions);
//                gifImages.add(imageThumbnail);
////                tempBitmapForSize.recycle();
//            }
//
//            // create the gif
//            // using -1 for both logical screen width and height to use the first frame dimension
//            writer.prepareForWrite(gif, -1, -1);
//            for (Bitmap image : gifImages) {
//                writer.writeFrame(gif, image, 1000);
//            }
//            writer.finishWrite(gif);
//            System.out.println("GIF CREATED!!!!!!!!!!!!!!!!!!!!!!!!!");
//
//            // delete temp created files
//            cleanThumbnails(thumbnailsPaths);
//            System.out.println("THUMBS DELETED!!!!!!!!!!!!!!!!!!!!!!");
//        } catch (Exception e) {
//            //TODO: ADD THAT if getting outOfMemory exception, run it again with lower quality
//            //TODO: thumbnails (i.e lower values for desiredWidth and desiredHeight)
//            // delete temp created files
//            cleanThumbnails(thumbnailsPaths);
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * create thumbnail for a given image.
//     * @param inFilePath image's path
//     * @param i image index (for the thumbnail name)
//     * @return thumbnail path
//     */
//    public String createThumbnail(String inFilePath, int i) {
//        String outThumbnail = "";
//        try{
//            File file = new File(inFilePath); // the image file
//            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
//
//            bitmapOptions.inJustDecodeBounds = true; // obtain the size of the image, without
//            // loading it in memory
//            BitmapFactory.decodeFile(file.getAbsolutePath(), bitmapOptions);
//
//            // find the best scaling factor for the desired dimensions
//            // (image resolution: https://microscope-microscope.org/microscope-info/image-resolution/)
//            int desiredWidth = 2080; // 400
//            int desiredHeight = 1542; // 300
//            float widthScale = (float)bitmapOptions.outWidth/desiredWidth;
//            float heightScale = (float)bitmapOptions.outHeight/desiredHeight;
//            float scale = Math.min(widthScale, heightScale);
//
//            int sampleSize = 1;
//            while (sampleSize < scale) {
//                sampleSize *= 2;
//            }
//            bitmapOptions.inSampleSize = sampleSize; // this value must be a power of 2,
//            // this is why we can't have an image scaled as we would like
//            bitmapOptions.inJustDecodeBounds = false; // now we want to load the image
//
//            // load just the part of the image necessary for creating the thumbnail (not the whole image)
//            Bitmap thumbnail = BitmapFactory.decodeFile(file.getAbsolutePath(), bitmapOptions);
//
//            // Save the thumbnail
//            String sourceJpgDirectoryPath = "/sdcard/media/";
////            String sourceJpgDirectoryPath = "/sdcard/";
//            outThumbnail = sourceJpgDirectoryPath + "_" + Integer.toString(i + 1) + "THUMB.jpg";
//            File thumbnailFile = new File(outThumbnail);
//            FileOutputStream fos = new FileOutputStream(thumbnailFile);
//            thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//            fos.flush();
//            fos.close();
//
//            // recycle the thumbnail
//            thumbnail.recycle();
//
//        }
//        catch (Exception e) {
//        e.printStackTrace();
//        }
//
//        return outThumbnail;
//    }
//
//    /**
//     * delete all thumbnails
//     * @param thumbnailsPaths arrayList of thumbnails Paths
//     */
//    public void cleanThumbnails(ArrayList<String> thumbnailsPaths) {
//        for (String path : thumbnailsPaths) {
//            deleteFile(new File (path));
//        }
//    }
//
//    public void deleteFile(File file) {
//        file.delete();
//        if(file.exists()){
//            try{
//                file.getCanonicalFile().delete();
//            }
//            catch (IOException e){
//                Log.d("delete file exception", "exception caused by " +
//                        "file.getCanonicalFile().delete() in deleteFile function");
//            }
////            if(file.exists()){
////                getApplicationContext().deleteFile(file.getName());
////            }
//        }
//    }
//
//}
//
//
//
//
////    /**
////     * pops up a toast message when forgot password button is clicked.
////     */
////    public void btn_loginAct_forgotPassword_clicked(View view) {
////        Toast.makeText(this, "Too bad :)", Toast.LENGTH_SHORT).show();
////
////////////////////   3   ////////////////////////////////////////////////
////// https://github.com/dragon66/android-gif-animated-writer
////
////        AsyncTask.execute(new Runnable() {
////            @Override
////            public void run() {
////                try {
////                    // True for dither. Will need more memory and CPU
////                    AnimatedGIFWriter writer = new AnimatedGIFWriter(true);
////                    OutputStream os = new FileOutputStream("/sdcard/media/test1.gif");
////                    ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
////                    for (int i = 0; i < 3; i++) {
////                        String sourceJpgDirectoryPath = "/sdcard/media/";
////                        String inFilePath = sourceJpgDirectoryPath + Integer.toString(i + 1) + ".jpg";
//////                    String inFilePath = sourceJpgDirectoryPath + "Img0000" + Integer.toString(i+1) + ".jpg";
////                        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
////                        Bitmap bitmap = BitmapFactory.decodeFile(inFilePath, bitmapOptions);
////                        bitmaps.add(bitmap);
////                    }
//////                    for (int i = 0; i < 3; i++) {
//////                        String sourceJpgDirectoryPath = "/sdcard/media/";
//////                        String inFilePath = sourceJpgDirectoryPath + Integer.toString(i + 1) + ".jpg";
////////                        String inFilePath = sourceJpgDirectoryPath + "Img0000" + Integer.toString(i+1) + ".jpg";
//////                        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
//////                        Bitmap bitmap = BitmapFactory.decodeFile(inFilePath, bitmapOptions);
//////                        bitmaps.add(bitmap);
//////                    }
////
////                    // Use -1 for both logical screen width and height to use the first frame dimension
////                    writer.prepareForWrite(os, -1, -1);
////
////                    for (Bitmap bitmap : bitmaps) {
////                        writer.writeFrame(os, bitmap, 1000);
////                    }
////                    writer.finishWrite(os);
////                    System.out.println("BANANAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
////                } catch (Exception e) {
////                    e.printStackTrace();
////                }
////            }
////        });
////    }
//////////////////   2   ////////////////////////////////////////////////
//// https://github.com/nbadal/android-gif-encoder/blob/master/GifEncoder.java
//
////        AsyncTask.execute(new Runnable() {
////            @Override
////            public void run() {
////                FileOutputStream outStream = null;
////                try{
////                    outStream = new FileOutputStream("/sdcard/media/test.gif");
////                    outStream.write(generateGIF());
////                    outStream.close();
////                }catch(Exception e){
////                    e.printStackTrace();
////                }
////            }
////        });
////    }
////    public byte[] generateGIF() {
////
//////        AsyncTask.execute(new Runnable() {
//////            @Override
//////            public void run() {
//////
//////            }
//////        });
////
////        ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
////
////        for(int i=0; i<2; i++){
////            String sourceJpgDirectoryPath = "/sdcard/media/";
////            String inFilePath = sourceJpgDirectoryPath + Integer.toString(i+1) + ".jpg";
//////            String inFilePath = sourceJpgDirectoryPath + "Img0000" + Integer.toString(i+1) + ".jpg";
////            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
////            Bitmap bitmap = BitmapFactory.decodeFile(inFilePath, bitmapOptions);
////            bitmaps.add(bitmap);
//////            ByteArrayOutputStream out = new ByteArrayOutputStream();
//////            bitmap.compress(Bitmap.CompressFormat.JPEG, 0, out);
//////            Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
//////            bitmaps.add(decoded);
////        }
////
////        ByteArrayOutputStream bos = new ByteArrayOutputStream();
////        AnimatedGifEncoder encoder = new AnimatedGifEncoder();
////        encoder.start(bos);
////        for (Bitmap bitmap : bitmaps) {
////            encoder.addFrame(bitmap);
////        }
////        encoder.finish();
////        System.out.println("BANANAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
////        return bos.toByteArray();
////    }
//
//
//////////////////   1   ////////////////////////////////////////////////
////  source: i don't remember...
////
////        AsyncTask.execute(new Runnable() {
////            @Override
////            public void run() {
////
////
////
////                GifEncoder gifEncoder = new GifEncoder();
////
////                String outFilePath = "/sdcard/media/test.gif";
////                try {
////                    gifEncoder.init(640, 480, outFilePath, GifEncoder.EncodingType.ENCODING_TYPE_NORMAL_LOW_MEMORY);
////                } catch (IOException e) {
////                }
////
////                int delayMs = 200;
////                for(int i=0; i<2; i++){
////                    String sourceJpgDirectoryPath = "/sdcard/media/";
////                    String inFilePath = sourceJpgDirectoryPath + "Img0000" + Integer.toString(i+1) + ".jpg";
////                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
////                    Bitmap bitmapToEncode = BitmapFactory.decodeFile(inFilePath, bitmapOptions);
//////                    bitmapToEncode= bitmapToEncode.copy(Bitmap.Config.ARGB_8888, true);
//////                    bitmapToEncode.reconfigure(640, 480, ARGB_8888);
////
////                    // Bitmap is MUST ARGB_8888.
////                    gifEncoder.encodeFrame(bitmapToEncode, delayMs);
////                }
////                gifEncoder.close();
////            }
////        });
////    }