/*
	Licensed to UbiCollab.org under one or more contributor
	license agreements.  See the NOTICE file distributed 
	with this work for additional information regarding
	copyright ownership. UbiCollab.org licenses this file
	to you under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance
	with the License. You may obtain a copy of the License at
	
	    http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing,
	software distributed under the License is distributed on an
	"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
	KIND, either express or implied.  See the License for the
	specific language governing permissions and limitations
	under the License.
*/

package org.ubicollab.nomad.home;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class PieChartView extends View {
	private static final int WAIT = 0;
	private static final int IS_READY_TO_DRAW = 1;
	private static final int IS_DRAW = 2;
	private static final float START_INC = 30;
	private Paint mBgPaints = new Paint();
	private Paint mLinePaints = new Paint();
	private int mWidth;
	private int mHeight;
	private int mGapLeft;
	private int mGapRight;
	private int mGapTop;
	private int mGapBottom;
	private int mBgColor;
	private int mState = WAIT;
	private float mStart;
	private float mSweep;
	private int mMaxConnection;
	private List<PieChart> mDataArray;

	public PieChartView(Context context) {
		super(context);
	}

	public PieChartView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (mState != IS_READY_TO_DRAW)
			return;
		canvas.drawColor(mBgColor);

		mBgPaints.setAntiAlias(true);
		mBgPaints.setStyle(Paint.Style.FILL);
		mBgPaints.setColor(0x88FF0000);
		mBgPaints.setStrokeWidth(0.5f);

		mLinePaints.setAntiAlias(true);
		mLinePaints.setStyle(Paint.Style.STROKE);
		mLinePaints.setColor(0xff000000);
		mLinePaints.setStrokeWidth(0.5f);

		RectF mOvals = new RectF(mGapLeft, mGapTop, mWidth - mGapRight, mHeight
				- mGapBottom);

		mStart = START_INC;
		PieChart Item;
		for (int i = 0; i < mDataArray.size(); i++) {
			Item = (PieChart) mDataArray.get(i);
			mBgPaints.setColor(Item.Color);
			mSweep = (float) 360
					* ((float) Item.Count / (float) mMaxConnection);

			System.out.println("Item.Count: " + Item.Count);
			System.out.println("MaxConn: " + mMaxConnection);

			canvas.drawArc(mOvals, mStart, mSweep, true, mBgPaints);
			canvas.drawArc(mOvals, mStart, mSweep, true, mLinePaints);
			mStart += mSweep;
		}

		Options options = new BitmapFactory.Options();
		options.inScaled = false;

		mState = IS_DRAW;
	}

	// --------------------------------------------------------------------------------------
	public void setGeometry(int width, int height, int GapLeft, int GapRight,
			int GapTop, int GapBottom) {
		mWidth = width;
		mHeight = height;
		mGapLeft = GapLeft;
		mGapRight = GapRight;
		mGapTop = GapTop;
		mGapBottom = GapBottom;
	}

	public void setSkinParams(int bgColor) {
		mBgColor = bgColor;
	}

	public void setData(List<PieChart> data, int MaxConnection) {
		mDataArray = data;
		mMaxConnection = MaxConnection;
		mState = IS_READY_TO_DRAW;
	}

	public void setState(int State) {
		mState = State;
	}

	public int getColorValue(int Index) {
		if (mDataArray == null)
			return 0;
		if (Index < 0) {
			return ((PieChart) mDataArray.get(0)).Color;
		} else if (Index >= mDataArray.size()) {
			return ((PieChart) mDataArray.get(mDataArray.size() - 1)).Color;
		} else {
			return ((PieChart) mDataArray.get(mDataArray.size() - 1)).Color;
		}
	}
}
