package com.tap.DAO;

import java.util.List;

import com.tap.model.Feedback;
import com.tap.model.FeedbackView;

public interface FeedbackDAO {
	int addFeedback(Feedback feedback); // one per order (orderId is UNIQUE)

	Feedback getByOrder(int orderId);

	List<FeedbackView> getAllViews();

	double getAverageRating();
}