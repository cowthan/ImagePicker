package com.desmond.squarecamera;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


/**
 *
 */
public class TrippleEditSavePhotoFragment extends Fragment {

    public static final String TAG = TrippleEditSavePhotoFragment.class.getSimpleName();
    public static final String BITMAP_KEY = "bitmap_byte_array";
    public static final String ROTATION_KEY = "rotation";
    public static final String IMAGE_INFO = "image_info";

    private static final int REQUEST_STORAGE = 1;

    public static Fragment newInstance(byte[] bitmapByteArray, int rotation,
                                       @NonNull ImageParameters parameters) {
        Fragment fragment = new TrippleEditSavePhotoFragment();

        Bundle args = new Bundle();
        args.putByteArray(BITMAP_KEY, bitmapByteArray);
        args.putInt(ROTATION_KEY, rotation);
        args.putParcelable(IMAGE_INFO, parameters);

        fragment.setArguments(args);
        return fragment;
    }

    public TrippleEditSavePhotoFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.squarecamera__fragment_edit_save_photo, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int rotation = getArguments().getInt(ROTATION_KEY);
        byte[] data = getArguments().getByteArray(BITMAP_KEY);
        ImageParameters imageParameters = getArguments().getParcelable(IMAGE_INFO);

        if (imageParameters == null) {
            return;
        }

        final ImageView photoImageView = (ImageView) view.findViewById(R.id.photo);

        imageParameters.mIsPortrait =
                getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        final View topView = view.findViewById(R.id.topView);
        if (imageParameters.mIsPortrait) {
            topView.getLayoutParams().height = imageParameters.mCoverHeight;
        } else {
            topView.getLayoutParams().width = imageParameters.mCoverWidth;
        }

        rotatePicture(rotation, data, photoImageView);

        view.findViewById(R.id.save_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePicture();
            }
        });
    }

    private void rotatePicture(int rotation, byte[] data, ImageView photoImageView) {
        Bitmap bitmap = ImageUtility.decodeSampledBitmapFromByte(getActivity(), data);
//        Log.d(TAG, "original bitmap width " + bitmap.getWidth() + " height " + bitmap.getHeight());
        if (rotation != 0) {
            Bitmap oldBitmap = bitmap;

            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);

            bitmap = Bitmap.createBitmap(
                    oldBitmap, 0, 0, oldBitmap.getWidth(), oldBitmap.getHeight(), matrix, false
            );

            oldBitmap.recycle();
        }

        photoImageView.setImageBitmap(bitmap);
    }

    private void savePicture() {
       // requestForPermission();
        final View view = getView();
        if (view != null) {
                ImageView photoImageView = (ImageView) view.findViewById(R.id.photo);

                Bitmap bitmap = ((BitmapDrawable) photoImageView.getDrawable()).getBitmap();
                Uri photoUri = ImageUtility.savePicture(getActivity(), bitmap);


                TrippleManager.photos.add(photoUri);
                if(TrippleManager.photos.size() ==3){
                    ((TrippleCameraActivity) getActivity()).returnPhotoUri(photoUri);
                }else{
                    //还得回去继续拍
                    ((TrippleCameraActivity) getActivity()).onCancel(null);
                }

            }
    }

//    private void requestForPermission() {
//        RuntimePermissionActivity.startActivity(EditSavePhotoFragment.this,
//                REQUEST_STORAGE,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE);
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Activity.RESULT_OK != resultCode) return;

//        if (REQUEST_STORAGE == requestCode && data != null) {
//            final boolean isGranted = data.getBooleanExtra(RuntimePermissionActivity.REQUESTED_PERMISSION, false);
//            final View view = getView();
//            if (isGranted && view != null) {
//                ImageView photoImageView = (ImageView) view.findViewById(R.id.photo);
//
//                Bitmap bitmap = ((BitmapDrawable) photoImageView.getDrawable()).getBitmap();
//                Uri photoUri = ImageUtility.savePicture(getActivity(), bitmap);
//
//                ((CameraActivity) getActivity()).returnPhotoUri(photoUri);
//            }
//        } else {
//            super.onActivityResult(requestCode, resultCode, data);
//        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
