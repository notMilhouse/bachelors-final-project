import React, { useState, useEffect } from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import { Plus, User, TrendingDown, TrendingUp, Calendar, Mail, Lock } from 'lucide-react';

const API_BASE_URL = 'http://localhost:8000/api'; // Update this with your API URL

export default function WeightTrackerDashboard() {
  const [profiles, setProfiles] = useState([]);
  const [selectedProfile, setSelectedProfile] = useState(null);
  const [measurements, setMeasurements] = useState([]);
  const [loading, setLoading] = useState(false);
  const [showProfileForm, setShowProfileForm] = useState(false);
  const [showMeasurementForm, setShowMeasurementForm] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);

  // Form states
  const [profileForm, setProfileForm] = useState({ name: '', email: '', password: '' });
  const [measurementForm, setMeasurementForm] = useState({ 
    value: '', 
    unit: 'kg', 
    date: new Date().toISOString().split('T')[0] 
  });

  // Fetch profiles on mount
  useEffect(() => {
    fetchProfiles();
  }, []);

  // Fetch measurements when profile is selected
  useEffect(() => {
    if (selectedProfile) {
      fetchMeasurements(selectedProfile.id);
    }
  }, [selectedProfile]);

  const fetchProfiles = async () => {
    setLoading(true);
    try {
      const response = await fetch(`${API_BASE_URL}/profiles`);
      const data = await response.json();
      setProfiles(data);
      if (data.length > 0 && !selectedProfile) {
        setSelectedProfile(data[0]);
      }
    } catch (error) {
      console.error('Error fetching profiles:', error);
    } finally {
      setLoading(false);
    }
  };

  const fetchMeasurements = async (profileId) => {
    setLoading(true);
    try {
      const response = await fetch(`${API_BASE_URL}/measurements/by-profile/${profileId}`);
      const data = await response.json();
      // Sort by createdAt or id if date field exists
      const sorted = data.sort((a, b) => {
        const dateA = a.createdAt ? new Date(a.createdAt) : new Date(a.id);
        const dateB = b.createdAt ? new Date(b.createdAt) : new Date(b.id);
        return dateA - dateB;
      });
      setMeasurements(sorted);
    } catch (error) {
      console.error('Error fetching measurements:', error);
      setMeasurements([]);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateProfile = async () => {
    if (!profileForm.name || !profileForm.email || !profileForm.password) {
      alert('Please fill all fields');
      return;
    }
    try {
      const response = await fetch(`${API_BASE_URL}/profiles`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          name: profileForm.name,
          email: profileForm.email,
          password: profileForm.password
        })
      });
      if (response.ok) {
        setProfileForm({ name: '', email: '', password: '' });
        setShowProfileForm(false);
        fetchProfiles();
      } else {
        const error = await response.text();
        alert(`Error: ${error}`);
      }
    } catch (error) {
      console.error('Error creating profile:', error);
      alert('Error creating profile');
    }
  };

  const handleCreateMeasurement = async () => {
    if (!selectedProfile || !measurementForm.value) {
      alert('Please fill all fields');
      return;
    }
    try {
      const response = await fetch(`${API_BASE_URL}/measurements`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          value: parseFloat(measurementForm.value),
          unit: measurementForm.unit,
          profileId: selectedProfile.id
        })
      });
      if (response.ok) {
        setMeasurementForm({ value: '', unit: 'kg', date: new Date().toISOString().split('T')[0] });
        setShowMeasurementForm(false);
        fetchMeasurements(selectedProfile.id);
      } else {
        const error = await response.text();
        alert(`Error: ${error}`);
      }
    } catch (error) {
      console.error('Error creating measurement:', error);
      alert('Error creating measurement');
    }
  };

  const deleteMeasurement = async (measurementId) => {
    if (!selectedProfile || !confirm('Delete this measurement?')) return;
    try {
      const response = await fetch(`${API_BASE_URL}/measurements/${measurementId}`, {
        method: 'DELETE'
      });
      if (response.ok) {
        fetchMeasurements(selectedProfile.id);
      }
    } catch (error) {
      console.error('Error deleting measurement:', error);
    }
  };

  const deleteProfile = async () => {
    if (!selectedProfile || !confirm(`Delete profile "${selectedProfile.name}"? This will also delete all measurements.`)) return;
    try {
      const response = await fetch(`${API_BASE_URL}/profiles/${selectedProfile.id}`, {
        method: 'DELETE'
      });
      if (response.ok) {
        setSelectedProfile(null);
        setMeasurements([]);
        fetchProfiles();
      }
    } catch (error) {
      console.error('Error deleting profile:', error);
    }
  };

  const getStats = () => {
    if (measurements.length === 0) return null;
    const current = measurements[measurements.length - 1].value;
    const start = measurements[0].value;
    const change = current - start;
    return { current, start, change, unit: measurements[0].unit };
  };

  const stats = getStats();

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto p-6">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-3xl font-light text-gray-900 mb-2">Weight Tracker</h1>
          <p className="text-gray-500">Monitor your progress over time</p>
        </div>

        {/* Profile Selector */}
        <div className="bg-white rounded-lg shadow-sm p-4 mb-6">
          <div className="flex items-center justify-between mb-2">
            <div className="flex items-center gap-4 flex-1">
              <User className="text-gray-400" size={20} />
              <select
                value={selectedProfile?.id || ''}
                onChange={(e) => {
                  const profile = profiles.find(p => p.id === parseInt(e.target.value));
                  setSelectedProfile(profile);
                }}
                disabled={profiles.length === 0}
                className="flex-1 max-w-xs border-gray-300 rounded-md text-sm focus:ring-gray-900 focus:border-gray-900"
              >
                {profiles.length === 0 && <option value="">No profiles</option>}
                {profiles.map(profile => (
                  <option key={profile.id} value={profile.id}>{profile.name}</option>
                ))}
              </select>
            </div>
            <div className="flex gap-2">
              {selectedProfile && (
                <button
                  onClick={deleteProfile}
                  className="px-4 py-2 text-red-600 border border-red-300 rounded-md hover:bg-red-50 text-sm"
                >
                  Delete Profile
                </button>
              )}
              <button
                onClick={() => setShowProfileForm(!showProfileForm)}
                className="flex items-center gap-2 px-4 py-2 bg-gray-900 text-white rounded-md hover:bg-gray-800 text-sm"
              >
                <Plus size={16} />
                New Profile
              </button>
            </div>
          </div>

          {selectedProfile && (
            <div className="flex items-center gap-2 text-sm text-gray-500 pl-9">
              <Mail size={14} />
              {selectedProfile.email}
            </div>
          )}

          {showProfileForm && (
            <div className="mt-4 pt-4 border-t space-y-3">
              <input
                type="text"
                placeholder="Full name"
                value={profileForm.name}
                onChange={(e) => setProfileForm({ ...profileForm, name: e.target.value })}
                className="w-full border-gray-300 rounded-md text-sm focus:ring-gray-900 focus:border-gray-900"
              />
              <input
                type="email"
                placeholder="Email address"
                value={profileForm.email}
                onChange={(e) => setProfileForm({ ...profileForm, email: e.target.value })}
                className="w-full border-gray-300 rounded-md text-sm focus:ring-gray-900 focus:border-gray-900"
              />
              <input
                type="password"
                placeholder="Password"
                value={profileForm.password}
                onChange={(e) => setProfileForm({ ...profileForm, password: e.target.value })}
                className="w-full border-gray-300 rounded-md text-sm focus:ring-gray-900 focus:border-gray-900"
              />
              <div className="flex gap-2">
                <button
                  onClick={handleCreateProfile}
                  className="px-4 py-2 bg-gray-900 text-white rounded-md hover:bg-gray-800 text-sm"
                >
                  Create Profile
                </button>
                <button
                  onClick={() => setShowProfileForm(false)}
                  className="px-4 py-2 border border-gray-300 rounded-md hover:bg-gray-50 text-sm"
                >
                  Cancel
                </button>
              </div>
            </div>
          )}
        </div>

        {selectedProfile && (
          <>
            {/* Stats Cards */}
            {stats && (
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
                <div className="bg-white rounded-lg shadow-sm p-4">
                  <div className="text-sm text-gray-500 mb-1">Current Weight</div>
                  <div className="text-2xl font-light">{stats.current.toFixed(1)} {stats.unit}</div>
                </div>
                <div className="bg-white rounded-lg shadow-sm p-4">
                  <div className="text-sm text-gray-500 mb-1">Starting Weight</div>
                  <div className="text-2xl font-light">{stats.start.toFixed(1)} {stats.unit}</div>
                </div>
                <div className="bg-white rounded-lg shadow-sm p-4">
                  <div className="text-sm text-gray-500 mb-1">Total Change</div>
                  <div className="flex items-center gap-2">
                    <span className="text-2xl font-light">
                      {stats.change > 0 ? '+' : ''}{stats.change.toFixed(1)} {stats.unit}
                    </span>
                    {stats.change < 0 ? (
                      <TrendingDown className="text-green-500" size={20} />
                    ) : stats.change > 0 ? (
                      <TrendingUp className="text-red-500" size={20} />
                    ) : null}
                  </div>
                </div>
              </div>
            )}

            {/* Chart */}
            {measurements.length > 0 && (
              <div className="bg-white rounded-lg shadow-sm p-6 mb-6">
                <h2 className="text-lg font-light text-gray-900 mb-4">Progress Chart</h2>
                <ResponsiveContainer width="100%" height={300}>
                  <LineChart data={measurements}>
                    <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
                    <XAxis
                      dataKey="createdAt"
                      stroke="#999"
                      style={{ fontSize: '12px' }}
                      tickFormatter={(date) => new Date(date).toLocaleDateString('en-US', { month: 'short', day: 'numeric' })}
                    />
                    <YAxis stroke="#999" style={{ fontSize: '12px' }} />
                    <Tooltip
                      contentStyle={{ background: 'white', border: '1px solid #e5e5e5', borderRadius: '6px' }}
                      labelFormatter={(date) => new Date(date).toLocaleDateString()}
                      formatter={(value) => [`${value} ${measurements[0]?.unit || 'kg'}`, 'Weight']}
                    />
                    <Line type="monotone" dataKey="value" stroke="#000" strokeWidth={2} dot={{ fill: '#000' }} />
                  </LineChart>
                </ResponsiveContainer>
              </div>
            )}

            {/* Measurements List */}
            <div className="bg-white rounded-lg shadow-sm p-6">
              <div className="flex items-center justify-between mb-4">
                <h2 className="text-lg font-light text-gray-900">Measurements</h2>
                <button
                  onClick={() => setShowMeasurementForm(!showMeasurementForm)}
                  className="flex items-center gap-2 px-4 py-2 bg-gray-900 text-white rounded-md hover:bg-gray-800 text-sm"
                >
                  <Plus size={16} />
                  Add Measurement
                </button>
              </div>

              {showMeasurementForm && (
                <div className="mb-4 pb-4 border-b flex gap-3">
                  <input
                    type="number"
                    step="0.1"
                    placeholder="Weight value"
                    value={measurementForm.value}
                    onChange={(e) => setMeasurementForm({ ...measurementForm, value: e.target.value })}
                    className="w-32 border-gray-300 rounded-md text-sm focus:ring-gray-900 focus:border-gray-900"
                  />
                  <select
                    value={measurementForm.unit}
                    onChange={(e) => setMeasurementForm({ ...measurementForm, unit: e.target.value })}
                    className="w-24 border-gray-300 rounded-md text-sm focus:ring-gray-900 focus:border-gray-900"
                  >
                    <option value="kg">kg</option>
                    <option value="lb">lb</option>
                  </select>
                  <button
                    onClick={handleCreateMeasurement}
                    className="px-4 py-2 bg-gray-900 text-white rounded-md hover:bg-gray-800 text-sm"
                  >
                    Add
                  </button>
                  <button
                    onClick={() => setShowMeasurementForm(false)}
                    className="px-4 py-2 border border-gray-300 rounded-md hover:bg-gray-50 text-sm"
                  >
                    Cancel
                  </button>
                </div>
              )}

              {loading ? (
                <div className="text-center py-8 text-gray-500">Loading...</div>
              ) : measurements.length === 0 ? (
                <div className="text-center py-8 text-gray-500">No measurements yet. Add your first one!</div>
              ) : (
                <div className="space-y-2">
                  {measurements.slice().reverse().map(measurement => (
                    <div
                      key={measurement.id}
                      className="flex items-center justify-between py-3 px-4 hover:bg-gray-50 rounded-md"
                    >
                      <div className="flex items-center gap-4">
                        <Calendar className="text-gray-400" size={16} />
                        <span className="text-sm text-gray-600">
                          {formatDate(measurement.createdAt)}
                        </span>
                      </div>
                      <div className="flex items-center gap-4">
                        <span className="text-lg font-light">
                          {measurement.value.toFixed(1)} {measurement.unit}
                        </span>
                        <button
                          onClick={() => deleteMeasurement(measurement.id)}
                          className="text-red-500 hover:text-red-700 text-sm"
                        >
                          Delete
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </>
        )}

        {!selectedProfile && !loading && profiles.length === 0 && (
          <div className="bg-white rounded-lg shadow-sm p-12 text-center">
            <User className="mx-auto text-gray-300 mb-4" size={48} />
            <p className="text-gray-500 mb-4">No profiles yet. Create your first profile to get started!</p>
            <button
              onClick={() => setShowProfileForm(true)}
              className="px-6 py-3 bg-gray-900 text-white rounded-md hover:bg-gray-800"
            >
              Create Profile
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
