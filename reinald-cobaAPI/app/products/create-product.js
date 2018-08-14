$(document).ready(function(){

    // show html form when 'create product' button was clicked
    $(document).on('click', '.create-product-button', function(){
        // // load list of categories
        // $.getJSON("http://localhost/coba-API/category/read.php", function(data){
        //     // build categories option html
        //     // loop through returned list of data
        //     var categories_options_html="";
        //     categories_options_html+="<select name='category_id' class='form-control'>";
        //     $.each(data.records, function(key, val){
        //         categories_options_html+="<option value='" + val.id + "'>" + val.name + "</option>";
        //     });
        //     categories_options_html+="</select>";
            // we have our html form here where product information will be entered
            // we used the 'required' html5 property to prevent empty fields
            var create_product_html="";

            // 'read products' button to show list of products
            create_product_html+="<div id='read-products' class='btn btn-primary pull-right m-b-15px read-products-button'>";
            create_product_html+="<span class='glyphicon glyphicon-list'></span> Read Products";
            create_product_html+="</div>";

            // 'create product' html form
            create_product_html+="<form id='create-product-form' action='#' method='post' border='0'>";
            create_product_html+="<table class='table table-hover table-responsive table-bordered'>";

            // name field
            create_product_html+="<tr>";
            create_product_html+="<td>Name</td>";
            create_product_html+="<td><input type='text' name='nama_lengkap' class='form-control' required /></td>";
            create_product_html+="</tr>";

            // name field
            create_product_html+="<tr>";
            create_product_html+="<td>email</td>";
            create_product_html+="<td><input type='text' name='email' class='form-control' required /></td>";
            create_product_html+="</tr>";

            // name field
            create_product_html+="<tr>";
            create_product_html+="<td>password</td>";
            create_product_html+="<td><input type='password' name='password' class='form-control' required /></td>";
            create_product_html+="</tr>";

            // name field
            create_product_html+="<tr>";
            create_product_html+="<td>noktp</td>";
            create_product_html+="<td><input type='text' name='no_ktp' class='form-control' required /></td>";
            create_product_html+="</tr>";

            // name field
            create_product_html+="<tr>";
            create_product_html+="<td>tgl lhr</td>";
            create_product_html+="<td><input type='date' name='tgl_lahir' class='form-control' required /></td>";
            create_product_html+="</tr>";

            // name field
            create_product_html+="<tr>";
            create_product_html+="<td>alamat</td>";
            create_product_html+="<td><input type='text' name='alamat' class='form-control' required /></td>";
            create_product_html+="</tr>";

            // name field
            create_product_html+="<tr>";
            create_product_html+="<td>kode rhs</td>";
            create_product_html+="<td><input type='text' name='kode_rahasia' class='form-control' required /></td>";
            create_product_html+="</tr>";
            // button to submit form
            create_product_html+="<tr>";
            create_product_html+="<td></td>";
            create_product_html+="<td>";
            create_product_html+="<button type='submit' class='btn btn-primary'>";
            create_product_html+="<span class='glyphicon glyphicon-plus'></span> daftar";
            create_product_html+="</button>";
            create_product_html+="</td>";
            create_product_html+="</tr>";

            create_product_html+="</table>";
            create_product_html+="</form>";

            // inject html to 'page-content' of our app
            $("#page-content").html(create_product_html);

// chage page title
            changePageTitle("Create Product");

        });
    });

    // will run if create product form was submitted
    $(document).on('submit', '#create-product-form', function(){
        // get form data
        var form_data=JSON.stringify($(this).serializeObject());

        // submit form data to api
        $.ajax({
            url: "http://localhost/mini-internet-banking/API/nasabah/create.php",
            type : "POST",
            contentType : 'application/json',
            data : form_data,
            success : function(result) {
                // product was created, go back to products list
                showProducts();
            },
            error: function(xhr, resp, text) {
                // show error to console
                console.log(xhr, resp, text);
            }
        });

        return false;
    });
