package sochat.so.com.android.live.livestreaming;///*
// * Copyright (C) 2012 CyberAgent
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// * http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package sochat.so.com.android.live.livestreaming;
//
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//
//import java.util.LinkedList;
//import java.util.List;
//
//import jp.co.cyberagent.android.gpuimage.GPUImageFaceFilter;
//import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
//
//
//public class GPUImageFilterTools {
//
//    private static int mPercentage;
//    private static GPUImageFilter aaa;
//    private static final String TAG = "NeteaseLiveStream";
//
//    public static void showDialog(final Context context,
//            final OnGpuImageFilterChosenListener listener) {
//        final FilterList filters = new FilterList();
//
//        filters.addFilter("美颜", FilterType.FACE);
//        filters.addFilter("普通", FilterType.NORMAL);
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle("滤镜模式选择");
//        builder.setItems(filters.names.toArray(new String[filters.names.size()]),
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(final DialogInterface dialog, final int item) {
//                        listener.onGpuImageFilterChosenListener(createFilterForType(context, filters.filters.get(item)));
//                    }
//                });
//        builder.create().show();
//    }
//
//    private static GPUImageFilter createFilterForType(final Context context, final FilterType type) {
//        switch (type) {
//            case FACE:
//        	    return new GPUImageFaceFilter();
//            case NORMAL:
//                return new GPUImageFilter();
//            default:
//                throw new IllegalStateException("No filter of that type!");
//        }
//
//    }
//
//    public interface OnGpuImageFilterChosenListener {
//        void onGpuImageFilterChosenListener(GPUImageFilter filter);
//    }
//
//    private enum FilterType {
//        FACE,
//        NORMAL
//    }
//
//    private static class FilterList {
//        public List<String> names = new LinkedList<String>();
//        public List<FilterType> filters = new LinkedList<FilterType>();
//
//        public void addFilter(final String name, final FilterType filter) {
//            names.add(name);
//            filters.add(filter);
//        }
//    }
//}
