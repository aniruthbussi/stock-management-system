import React, { useState, useEffect } from "react";
import Layout from "../component/Layout";
import ApiService from "../service/ApiService";
import { useNavigate } from "react-router-dom";
import PaginationComponent from "../component/PaginationComponent";

const TransactionsPage = () => {
    const [transactions, setTransactions] = useState([]);
    const [message, setMessage] = useState("");
    const [filter, setFilter] = useState("");
    const [valueToSearch, setValueToSearch] = useState("");

    const navigate = useNavigate();

    //Pagination Set-Up
    const [currentPage, setCurrentPage] = useState(1);
    const [totalPages, setTotalPages] = useState(0);
    const itemsPerPage = 10;

    useEffect(()=>{
        const getTransactions = async()=> {

            try {
                const transactionData = await ApiService.getAllTransactions(valueToSearch);
            
                if (transactionData.status === 200) {
                
                    setTotalPages(Math.ceil(transactionData.transactions.length / itemsPerPage));
    
                    setTransactions(
                        transactionData.transactions.slice(
                            (currentPage - 1) * itemsPerPage,
                            currentPage * itemsPerPage
                        )
                    );
                }

            } catch(error){
                showMessage(
                    error.response?.data?.message || "Error Getting Transactions: " + error
                );
            }
        };

        getTransactions();
    }, [currentPage, valueToSearch]);

        //method to Show Message or Errors
     const showMessage = (msg) => {
        setMessage(msg);
        setTimeout(() => {
            setMessage("");
        }, 4000);
    };

    //Handle Search
    const handleSearch = () => {
        console.log("Search Hit");
        console.log("Filter is :" + filter);
                
        setCurrentPage(1)
        setValueToSearch(filter)
    }

    //Navigate to Transactions Details Page
    const navigateToTransactionDetailsPage = (transactionId) => {
        navigate(`/transaction/${transactionId}`);
    }

return(
    <Layout>
        
        {message && <div className="message">{message}</div>}
        <div className="transactions-page">
          <div className="transactions-header">
            <h1>Transactions</h1>
            <div className="transaction-search">
                <input 
                placeholder="Search transaction ..."
                value={filter}
                onChange={(e)=> setFilter(e.target.value)}
                type="text" />
                <button onClick={()=> handleSearch()}>Search</button>
            </div>
          </div>

          { transactions && 
            <table className="transactions-table">
                <thead>
                    <tr>
                        <th>TYPE</th>
                        <th>STATUS</th>
                        <th>TOTAL PRICE</th>
                        <th>NO OF PRODUCTS</th>
                        <th>DATE</th>
                        <th>ACTIONS</th>
                    </tr>
                </thead>
                <tbody>
                    {transactions.map((transaction) => (
                        <tr key={transaction.id}>
                            <td>{transaction.transactionType}</td>
                            <td>{transaction.status}</td>
                            <td>{transaction.totalPrice}</td>
                            <td>{transaction.totalProducts}</td>
                            <td>{new Date(transaction.createdAt).toLocaleString()}</td>

                            <td>
                                <button onClick={()=> navigateToTransactionDetailsPage(transaction.id)}>View Details</button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
          }
        </div>
        
        <PaginationComponent
        currentPage={currentPage}
        totalPages={totalPages}
        onPageChange={setCurrentPage}
        />
    </Layout>
);
};
export default TransactionsPage;