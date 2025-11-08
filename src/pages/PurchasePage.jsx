import React, { useState, useEffect } from "react";
import Layout from "../component/Layout";
import ApiService from "../service/ApiService";

const PurchasePage = () => {
    const [products, setProducts] = useState([]);
    const [suppliers, setSuppliers] = useState([]);
    const [productId, setProductId] = useState("");
    const [supplierId, setSupplierId] = useState("");
    const [description, setDescription] = useState("");
    const [note, setNote] = useState("");
    const [quantity, setQuantity] = useState("");
    const [message, setMessage] = useState("");

    useEffect(()=>{
        const fetchproductsAndSuppliers = async () =>{
            try{
                const productData = await ApiService.getAllProducts();
                const supplierData = await ApiService.getAllSuppliers();
                setProducts(productData.products)
                setSuppliers(supplierData.suppliers)
            } catch(error){
                showMessage(
                    error.response?.data?.message || "Error Getting Products: " + error
                );
            }
        };

        fetchproductsAndSuppliers();
    }, []);


    const handleSubmit = async(e) =>{
        e.preventDefault();

        if(!productId || !supplierId || !quantity){
            showMessage("Please fill in all required fields")
            return
        }
        const body = {
            productId,
            quantity: parseInt(quantity),
            supplierId,
            description,
            note
        };
        console.log(body);
        

        try{
            const response = await ApiService.purchaseProduct(body);
            showMessage(response.message);
            resetForm();
        } catch(error){
            showMessage(
                error.response?.data?.message || "Error Getting Products: " + error
            );
        }
    }

    const resetForm = () =>{
        setProductId("");
        setSupplierId("");
        setDescription("");
        setNote("");
        setQuantity("");
    };

     //Method to show message or errors
  const showMessage = (msg) => {
    setMessage(msg);
    setTimeout(() => {
      setMessage("");
    }, 4000);
  };


  return(
    <Layout>
        {message && <div className="message">{message}</div>}
        <div className="purchase-form-page">
            <h1>Receive Inventory</h1>
            <form onSubmit={handleSubmit}>

            <div className="form-group">
            <label>Select Products</label>
            <select
              value={productId}
              onChange={(e) => setProductId(e.target.value)}
              required
            >
              <option value="">Select a Product</option>
              {products.map((product) => (
                <option key={product.id} value={product.id}>
                  {product.name}
                </option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label>Select Supplier</label>
            <select
              value={supplierId}
              onChange={(e) => setSupplierId(e.target.value)}
              required
            >
              <option value="">Select a Supplier</option>
              {suppliers.map((supplier) => (
                <option key={supplier.id} value={supplier.id}>
                  {supplier.name}
                </option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label>Description</label>
            <input
              type="text"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label>Note</label>
            <input
              type="text"
              value={note}
              onChange={(e) => setNote(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label>Quantity</label>
            <input
              type="number"
              value={quantity}
              onChange={(e) => setQuantity(e.target.value)}
              required
            />
          </div>

          <button type="submit">Purchase Product</button>
        </form>
        </div>
    </Layout>
  )

};
export default PurchasePage;