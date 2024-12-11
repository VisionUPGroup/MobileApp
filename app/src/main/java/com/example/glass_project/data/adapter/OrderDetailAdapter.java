package com.example.glass_project.data.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.glass_project.R;
import com.example.glass_project.data.model.order.OrderDetail;
import com.example.glass_project.data.model.order.OrderPaymentDetail;
import com.example.glass_project.data.model.order.Lens;
import com.example.glass_project.data.model.order.ProductGlassPayment;
import com.example.glass_project.data.model.order.ProductGlasses;

import java.text.DecimalFormat;
import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.ViewHolder> {

    private Context context;
    private List<OrderDetail> orderDetailsList;
    private OrderPaymentDetail orderPaymentDetail;
    private DecimalFormat decimalFormat = new DecimalFormat("#,###");

    public OrderDetailAdapter(Context context, List<OrderDetail> orderDetailsList, OrderPaymentDetail orderPaymentDetail) {
        this.context = context;
        this.orderDetailsList = orderDetailsList;
        this.orderPaymentDetail = orderPaymentDetail;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderDetail orderDetail = orderDetailsList.get(position);
        ProductGlasses productGlassDetail = orderDetail.getProductGlass().getProductGlassDetail();

        // Set product name and price
        holder.txtProductName.setText(orderDetail.getProductGlass().getEyeGlass().getName());
        holder.txtPrice.setText("đ" + decimalFormat.format(orderDetail.getProductGlass().getTotal()));
        holder.txtQuantity.setText("Số lượng: " + orderDetail.getQuantity());
        holder.layoutOD.setVisibility(View.GONE);
        holder.layoutOS.setVisibility(View.GONE);
        holder.txtPD.setVisibility(View.GONE);
        holder.txtShowMore.setOnClickListener(v -> {
            // Kiểm tra xem layoutOD và layoutOS có đang ẩn hay không
            if (holder.layoutOD.getVisibility() == View.GONE) {
                // Nếu đang ẩn, hiển thị các thông tin OD, OS và PD
                holder.layoutOD.setVisibility(View.VISIBLE);
                holder.layoutOS.setVisibility(View.VISIBLE);
                holder.txtPD.setVisibility(View.VISIBLE);
                holder.txtShowMore.setText("Ẩn bớt");
            } else {
                // Nếu đang hiển thị, ẩn chúng đi
                holder.layoutOD.setVisibility(View.GONE);
                holder.layoutOS.setVisibility(View.GONE);
                holder.txtPD.setVisibility(View.GONE);
                holder.txtShowMore.setText("Xem thêm");
            }
        });
        // Load product image
        if (orderDetail.getProductGlass().getEyeGlass().getEyeGlassImages() != null &&
                !orderDetail.getProductGlass().getEyeGlass().getEyeGlassImages().isEmpty()) {
            String imageUrl = orderDetail.getProductGlass().getEyeGlass().getEyeGlassImages().get(0).getUrl();
            Glide.with(context).load(imageUrl).into(holder.imgProduct);
        }

        // Calculate and set left and right lens prices
        double lensPrice = (orderDetail.getProductGlass().getTotal() - orderDetail.getProductGlass().getEyeGlass().getPrice()) / 2;
        holder.txtLeftLensPrice.setText("đ" + decimalFormat.format(lensPrice));
        holder.txtRightLensPrice.setText("đ" + decimalFormat.format(lensPrice));

        // Match and display left and right lens images and names
        if (orderPaymentDetail != null && orderPaymentDetail.getProductGlassPayments() != null) {
            for (ProductGlassPayment paymentDetail : orderPaymentDetail.getProductGlassPayments()) {
                if (paymentDetail.getProductGlassID() == orderDetail.getProductGlass().getId()) {
                    Lens leftLens = paymentDetail.getLeftLens();
                    Lens rightLens = paymentDetail.getRightLens();

                    // Set left lens details
                    if (leftLens != null) {
                        holder.txtLeftLensName.setText( "Lens Trái:"+leftLens.getLensName());
                        Glide.with(context).load(leftLens.getLensImage()).into(holder.imgLeftLens);
                    } else {
                        holder.txtLeftLensName.setText("Lens Trái: Không có");
                        holder.imgLeftLens.setImageResource(R.drawable.default_image);
                    }

                    // Set right lens details
                    if (rightLens != null) {
                        holder.txtRightLensName.setText( "Lens Phải:"+rightLens.getLensName());
                        Glide.with(context).load(rightLens.getLensImage()).into(holder.imgRightLens);
                    } else {
                        holder.txtRightLensName.setText("Lens Phải: Không có");
                        holder.imgRightLens.setImageResource(R.drawable.default_image);
                    }
                    break; // Found matching productGlassID, break loop
                }
            }
        }

        // Display sphere, cylinder, axis, add, and PD values for OD and OS
        if (productGlassDetail != null) {
            holder.txtSphereOD.setText("Cầu OD: " + productGlassDetail.getSphereOD());
            holder.txtCylinderOD.setText("Trụ OD: " + productGlassDetail.getCylinderOD());
            holder.txtAxisOD.setText("Trục OD: " + productGlassDetail.getAxisOD());
            holder.txtSphereOS.setText("Cầu OS: " + productGlassDetail.getSphereOS());
            holder.txtCylinderOS.setText("Trụ OS: " + productGlassDetail.getCylinderOS());
            holder.txtAxisOS.setText("Trục OS: " + productGlassDetail.getAxisOS());
            holder.txtAddOD.setText("Cộng thêm OD: " + productGlassDetail.getAddOD());
            holder.txtAddOS.setText("Cộng thêm OS: " + productGlassDetail.getAddOS());
            holder.txtPD.setText("Khoảng cách đồng tử (PD): " + productGlassDetail.getPd());
        }

    }

    @Override
    public int getItemCount() {
        return orderDetailsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtProductName, txtPrice, txtQuantity, txtLeftLensPrice, txtRightLensPrice;
        TextView txtLeftLensName, txtRightLensName;
        ImageView imgProduct, imgLeftLens, imgRightLens;
        TextView txtSphereOD, txtCylinderOD, txtAxisOD, txtSphereOS, txtCylinderOS, txtAxisOS, txtAddOD, txtAddOS, txtPD,txtShowMore;

        LinearLayout  layoutOS,layoutOD;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            txtLeftLensName = itemView.findViewById(R.id.txtLeftLensName);
            txtRightLensName = itemView.findViewById(R.id.txtRightLensName);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            imgLeftLens = itemView.findViewById(R.id.imgLeftLens);
            imgRightLens = itemView.findViewById(R.id.imgRightLens);

            txtSphereOD = itemView.findViewById(R.id.txtSphereOD);
            txtCylinderOD = itemView.findViewById(R.id.txtCylinderOD);
            txtAxisOD = itemView.findViewById(R.id.txtAxisOD);
            txtSphereOS = itemView.findViewById(R.id.txtSphereOS);
            txtCylinderOS = itemView.findViewById(R.id.txtCylinderOS);
            txtAxisOS = itemView.findViewById(R.id.txtAxisOS);
            layoutOD = itemView.findViewById(R.id.layoutOD);
            layoutOS = itemView.findViewById(R.id.layoutOS);
            txtPD = itemView.findViewById(R.id.txtPD);
            txtAddOD = itemView.findViewById(R.id.txtAddOD);
            txtAddOS = itemView.findViewById(R.id.txtAddOS);
            txtLeftLensPrice = itemView.findViewById(R.id.txtLeftLensPrice);
            txtRightLensPrice = itemView.findViewById(R.id.txtRightLensPrice);
            txtShowMore = itemView.findViewById(R.id.txtShowMore);
        }
    }
}
